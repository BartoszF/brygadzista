package pl.bfelis.brygadzista.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.plugin
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.SuspendingActionDispatcher
import pl.bfelis.brygadzista.SuspendingActionInterceptor
import pl.bfelis.brygadzista.UnsupportedActionException

/** Installs Brygadzista action dispatching into a Ktor application. */
object Brygadzista : BaseApplicationPlugin<Application, Brygadzista.Configuration, KtorActionDispatcher> {
    override val key = AttributeKey<KtorActionDispatcher>("Brygadzista")

    override fun install(
        pipeline: Application,
        configure: Configuration.() -> Unit,
    ): KtorActionDispatcher = Configuration().apply(configure).toDispatcher()

    /** Registers the application's action handlers and extensions. */
    class Configuration {
        private val handlers = mutableMapOf<Class<*>, RegisteredHandler>()
        private val contextFactories = mutableListOf<KtorActionContextFactory>()
        private val interceptors = mutableListOf<SuspendingActionInterceptor>()

        /** Registers a handler using the default [KtorActionContext]. */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified A : Action<R>, R> handler(
            noinline handler: suspend (KtorActionContext<A>) -> R,
        ) {
            register(A::class.java, KtorActionContext::class.java) { context ->
                handler(context as KtorActionContext<A>)
            }
        }

        /** Registers a handler that requires a custom [ActionContext] subtype. */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified A : Action<R>, R, C : ActionContext<A>> handler(
            contextClass: KClass<C>,
            noinline handler: suspend (C) -> R,
        ) {
            register(A::class.java, contextClass.java) { context -> handler(context as C) }
        }

        /** Registers a reusable handler descriptor. */
        fun handler(handler: KtorActionHandler) {
            register(handler.actionClass, handler.contextClass, handler.invoke)
        }

        @PublishedApi
        internal fun register(
            actionClass: Class<*>,
            contextClass: Class<*>,
            handler: suspend (ActionContext<*>) -> Any?,
        ) {
            check(handlers.putIfAbsent(actionClass, RegisteredHandler(contextClass, handler)) == null) {
                "Multiple action handlers registered for ${actionClass.name}"
            }
        }

        /** Adds a context factory. The first factory returning a context wins. */
        fun contextFactory(factory: KtorActionContextFactory) {
            contextFactories += factory
        }

        /** Adds an interceptor. Interceptors run in registration order, outermost first. */
        fun interceptor(interceptor: SuspendingActionInterceptor) {
            interceptors += interceptor
        }

        internal fun toDispatcher() = KtorActionDispatcher(handlers.toMap(), contextFactories.toList(), interceptors.toList())
    }
}

/** Dispatches the current call's actions through the installed [Brygadzista] plugin. */
val ApplicationCall.actionDispatcher: SuspendingActionDispatcher
    get() = application.plugin(Brygadzista).dispatcherFor(this)

class KtorActionDispatcher internal constructor(
    private val handlers: Map<Class<*>, RegisteredHandler>,
    private val contextFactories: List<KtorActionContextFactory>,
    private val interceptors: List<SuspendingActionInterceptor>,
) {
    internal fun dispatcherFor(call: ApplicationCall): SuspendingActionDispatcher =
        object : SuspendingActionDispatcher {
            override suspend fun <R> dispatch(action: Action<R>): R = dispatch(call, action, this)
        }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <R> dispatch(
        call: ApplicationCall,
        action: Action<R>,
        dispatcher: SuspendingActionDispatcher,
    ): R {
        val handler =
            handlers[action.javaClass]
                ?: throw UnsupportedActionException(action.javaClass as Class<out Action<*>>)
        val context =
            contextFactories.firstNotNullOfOrNull { it.create(call, action, dispatcher) }
                ?: KtorActionContext(action, call, dispatcher)
        require(handler.contextClass.isInstance(context)) {
            "Action handler for ${action.javaClass.name} requires context ${handler.contextClass.name}, " +
                "but ${context.javaClass.name} was created"
        }

        val interceptorContext = context as ActionContext<Action<R>>
        var proceed: suspend () -> R = { handler.invoke(context) as R }
        interceptors.asReversed().forEach { interceptor ->
            val next = proceed
            proceed = { interceptor.intercept(interceptorContext, next) }
        }
        return proceed()
    }
}

internal class RegisteredHandler(
    val contextClass: Class<*>,
    val invoke: suspend (ActionContext<*>) -> Any?,
)

/**
 * A typed Ktor action handler that can be registered directly or supplied by a DI integration.
 *
 * Create instances with [ktorActionHandler] or [contextualKtorActionHandler].
 */
class KtorActionHandler @PublishedApi internal constructor(
    @PublishedApi internal val actionClass: Class<*>,
    @PublishedApi internal val contextClass: Class<*>,
    @PublishedApi internal val invoke: suspend (ActionContext<*>) -> Any?,
)

/** Creates a handler that receives the default [KtorActionContext]. */
@Suppress("UNCHECKED_CAST")
inline fun <reified A : Action<R>, R> ktorActionHandler(
    noinline handler: suspend (KtorActionContext<A>) -> R,
): KtorActionHandler =
    KtorActionHandler(A::class.java, KtorActionContext::class.java) { context ->
        handler(context as KtorActionContext<A>)
    }

/** Creates a handler that requires a custom [ActionContext] subtype. */
@Suppress("UNCHECKED_CAST")
inline fun <reified A : Action<R>, R, reified C : ActionContext<A>> contextualKtorActionHandler(
    noinline handler: suspend (C) -> R,
): KtorActionHandler =
    KtorActionHandler(A::class.java, C::class.java) { context -> handler(context as C) }
