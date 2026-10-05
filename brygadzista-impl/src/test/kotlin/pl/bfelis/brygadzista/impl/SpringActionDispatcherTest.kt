package pl.bfelis.brygadzista.impl

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionDispatcher
import pl.bfelis.brygadzista.ActionHandler
import pl.bfelis.brygadzista.UnsupportedActionException
import kotlin.test.Test

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

        val thrown = assertFailsWith<IllegalStateException> {
            dispatcher.dispatch(FailingAction(failure))
        }

        assertSame(failure, thrown)
        context.close()
    }

    @Test
    fun `duplicate handlers fail during startup`() {
        val exception = assertFailsWith<Throwable> {
            AnnotationConfigApplicationContext(DuplicateHandlerConfiguration::class.java)
        }

        assertTrue(exception.allMessages().contains("Multiple @ActionHandler methods registered"))
    }

    @Test
    fun `malformed handlers fail during startup`() {
        val exception = assertFailsWith<Throwable> {
            AnnotationConfigApplicationContext(MalformedHandlerConfiguration::class.java)
        }

        assertTrue(exception.allMessages().contains("must accept ActionContext<A>"))
    }

    data class GreetingAction(val name: String) : Action<String>

    data class NestedGreetingAction(val name: String) : Action<String>

    data class FailingAction(val failure: IllegalStateException) : Action<Unit>

    data object UnhandledAction : Action<String>

    class GreetingHandler {
        @ActionHandler
        fun handle(context: ActionContext<GreetingAction>): String = "Hello ${context.action.name}"
    }

    class NestedGreetingHandler(private val dispatcher: ActionDispatcher) {
        @ActionHandler
        fun handle(context: ActionContext<NestedGreetingAction>): String =
            dispatcher.dispatch(GreetingAction(context.action.name))
    }

    class ExceptionHandler {
        @ActionHandler
        fun handle(context: ActionContext<FailingAction>) {
            throw context.action.failure
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
}

private fun Throwable.allMessages(): String =
    generateSequence(this) { it.cause }.joinToString("\n") { it.message.orEmpty() }
