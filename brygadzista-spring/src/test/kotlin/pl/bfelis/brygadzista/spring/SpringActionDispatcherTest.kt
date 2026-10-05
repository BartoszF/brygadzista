package pl.bfelis.brygadzista.spring

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionContextFactory
import pl.bfelis.brygadzista.ActionDispatcher
import pl.bfelis.brygadzista.ActionHandler
import pl.bfelis.brygadzista.ActionInterceptor
import pl.bfelis.brygadzista.UnsupportedActionException
import org.springframework.core.annotation.Order
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SpringActionDispatcherTest {
    @Test
    fun `auto configuration dispatches actions and supports nested dispatch`() {
        val context = AnnotationConfigApplicationContext(AutoConfigurationTestConfiguration::class.java)
        val dispatcher = context.getBean(ActionDispatcher::class.java)

        assertEquals("Hello Ada", dispatcher.dispatch(GreetingAction("Ada")))
        assertEquals("Hello Ada", dispatcher.dispatch(NestedGreetingAction("Ada")))
        assertFailsWith<UnsupportedActionException> {
            dispatcher.dispatch(UnhandledAction)
        }

        context.close()
    }

    @Test
    fun `handler exceptions are propagated unchanged`() {
        val context = AnnotationConfigApplicationContext(ExceptionHandlerConfiguration::class.java)
        val dispatcher = context.getBean(ActionDispatcher::class.java)
        val failure = IllegalStateException("handler failed")

        val thrown =
            assertFailsWith<IllegalStateException> {
                dispatcher.dispatch(FailingAction(failure))
            }

        assertSame(failure, thrown)
        context.close()
    }

    @Test
    fun `context factories select an ordered custom context`() {
        val context = AnnotationConfigApplicationContext(CustomContextConfiguration::class.java)
        val dispatcher = context.getBean(ActionDispatcher::class.java)

        assertEquals("Hello Ada from custom", dispatcher.dispatch(CustomGreetingAction("Ada")))

        context.close()
    }

    @Test
    fun `context factory fallback supplies the base context`() {
        val context = AnnotationConfigApplicationContext(AutoConfigurationTestConfiguration::class.java)
        val dispatcher = context.getBean(ActionDispatcher::class.java)

        assertEquals("Hello Ada", dispatcher.dispatch(GreetingAction("Ada")))

        context.close()
    }

    @Test
    fun `custom context mismatch fails before handler invocation`() {
        val context = AnnotationConfigApplicationContext(MismatchedContextConfiguration::class.java)
        val dispatcher = context.getBean(ActionDispatcher::class.java)

        val exception = assertFailsWith<IllegalArgumentException> {
            dispatcher.dispatch(CustomGreetingAction("Ada"))
        }

        assertTrue(exception.message.orEmpty().contains("requires context"))
        context.close()
    }

    @Test
    fun `interceptors wrap successful and failing actions`() {
        val context = AnnotationConfigApplicationContext(InterceptorConfiguration::class.java)
        val dispatcher = context.getBean(ActionDispatcher::class.java)
        val events = context.getBean(InterceptorEvents::class.java)

        assertEquals("Hello Ada", dispatcher.dispatch(GreetingAction("Ada")))
        assertEquals(listOf("outer-before", "before", "handler", "after", "outer-after"), events.values)

        events.values.clear()
        val failure = IllegalStateException("handler failed")
        assertFailsWith<IllegalStateException> { dispatcher.dispatch(FailingAction(failure)) }
        assertEquals(listOf("outer-before", "before", "failure", "outer-failure"), events.values)

        context.close()
    }

    @Test
    fun `duplicate handlers fail during startup`() {
        val exception =
            assertFailsWith<Throwable> {
                AnnotationConfigApplicationContext(DuplicateHandlerConfiguration::class.java)
            }

        assertTrue(exception.allMessages().contains("Multiple @ActionHandler methods registered"))
    }

    @Test
    fun `malformed handlers fail during startup`() {
        val exception =
            assertFailsWith<Throwable> {
                AnnotationConfigApplicationContext(MalformedHandlerConfiguration::class.java)
            }

        assertTrue(exception.allMessages().contains("must accept ActionContext<A>"))
    }

    data class GreetingAction(
        val name: String,
    ) : Action<String>

    data class NestedGreetingAction(
        val name: String,
    ) : Action<String>

    data class FailingAction(
        val failure: IllegalStateException,
    ) : Action<Unit>

    data class CustomGreetingAction(
        val name: String,
    ) : Action<String>

    data object UnhandledAction : Action<String>

    class GreetingHandler {
        @ActionHandler
        fun handle(context: ActionContext<GreetingAction>): String = "Hello ${context.action.name}"
    }

    class NestedGreetingHandler(
        private val dispatcher: ActionDispatcher,
    ) {
        @ActionHandler
        fun handle(context: ActionContext<NestedGreetingAction>): String = dispatcher.dispatch(GreetingAction(context.action.name))
    }

    class ExceptionHandler {
        @ActionHandler
        fun handle(context: ActionContext<FailingAction>): Unit = throw context.action.failure
    }

    class CustomGreetingContext(
        override val action: CustomGreetingAction,
    ) : ActionContext<CustomGreetingAction>(action)

    class CustomGreetingHandler {
        @ActionHandler
        fun handle(context: CustomGreetingContext): String = "Hello ${context.action.name} from custom"
    }

    class CustomGreetingFactory : ActionContextFactory {
        override fun create(action: Action<*>): ActionContext<*>? =
            (action as? CustomGreetingAction)?.let(::CustomGreetingContext)
    }

    class LaterCustomGreetingFactory : ActionContextFactory {
        override fun create(action: Action<*>): ActionContext<*>? =
            if (action is CustomGreetingAction) ActionContext(action) else null
    }

    class MismatchedGreetingHandler {
        @ActionHandler
        fun handle(context: CustomGreetingContext): String = context.action.name
    }

    class MismatchedGreetingFactory : ActionContextFactory {
        override fun create(action: Action<*>): ActionContext<*>? =
            if (action is CustomGreetingAction) ActionContext(action) else null
    }

    class InterceptorEvents {
        val values = mutableListOf<String>()
    }

    @Order(0)
    class RecordingInterceptor(
        private val events: InterceptorEvents,
    ) : ActionInterceptor {
        override fun <R> intercept(context: ActionContext<Action<R>>, proceed: () -> R): R {
            events.values += "before"
            return try {
                proceed().also { events.values += "after" }
            } catch (exception: RuntimeException) {
                events.values += "failure"
                throw exception
            }
        }
    }

    @Order(-1)
    class OuterRecordingInterceptor(
        private val events: InterceptorEvents,
    ) : ActionInterceptor {
        override fun <R> intercept(context: ActionContext<Action<R>>, proceed: () -> R): R {
            events.values += "outer-before"
            return try {
                proceed().also { events.values += "outer-after" }
            } catch (exception: RuntimeException) {
                events.values += "outer-failure"
                throw exception
            }
        }
    }

    class RecordingGreetingHandler(
        private val events: InterceptorEvents,
    ) {
        @ActionHandler
        fun handle(context: ActionContext<GreetingAction>): String {
            events.values += "handler"
            return "Hello ${context.action.name}"
        }
    }

    class FirstDuplicateHandler {
        @ActionHandler
        fun handle(context: ActionContext<GreetingAction>) = context.action.name
    }

    class SecondDuplicateHandler {
        @ActionHandler
        fun handle(context: ActionContext<GreetingAction>) = context.action.name
    }

    class MalformedHandler {
        @ActionHandler
        fun handle(action: GreetingAction) = action.name
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class ExceptionHandlerConfiguration {
        @Bean
        fun exceptionHandler() = ExceptionHandler()
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class AutoConfigurationTestConfiguration {
        @Bean
        fun greetingHandler() = GreetingHandler()

        @Bean
        fun nestedGreetingHandler(dispatcher: ActionDispatcher) = NestedGreetingHandler(dispatcher)
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class DuplicateHandlerConfiguration {
        @Bean
        fun firstDuplicateHandler() = FirstDuplicateHandler()

        @Bean
        fun secondDuplicateHandler() = SecondDuplicateHandler()
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class MalformedHandlerConfiguration {
        @Bean
        fun malformedHandler() = MalformedHandler()
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class CustomContextConfiguration {
        @Bean
        fun customGreetingHandler() = CustomGreetingHandler()

        @Bean
        @Order(0)
        fun customGreetingFactory() = CustomGreetingFactory()

        @Bean
        @Order(1)
        fun laterCustomGreetingFactory() = LaterCustomGreetingFactory()
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class MismatchedContextConfiguration {
        @Bean
        fun mismatchedGreetingHandler() = MismatchedGreetingHandler()

        @Bean
        @Order(0)
        fun mismatchedGreetingFactory() = MismatchedGreetingFactory()
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    class InterceptorConfiguration {
        @Bean
        fun interceptorEvents() = InterceptorEvents()

        @Bean
        fun recordingInterceptor(events: InterceptorEvents) = RecordingInterceptor(events)

        @Bean
        fun outerRecordingInterceptor(events: InterceptorEvents) = OuterRecordingInterceptor(events)

        @Bean
        fun recordingGreetingHandler(events: InterceptorEvents) = RecordingGreetingHandler(events)

        @Bean
        fun exceptionHandler() = ExceptionHandler()
    }
}

private fun Throwable.allMessages(): String = generateSequence(this) { it.cause }.joinToString("\n") { it.message.orEmpty() }
