package pl.bfelis.brygadzista.ktor

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.SuspendingActionInterceptor

class KtorActionDispatcherTest {
    @Test
    fun `dispatches through a route and supports nested actions`() = testApplication {
        application {
            install(Brygadzista) {
                handler<GreetingAction, String> { "Hello ${it.action.name}" }
                handler<NestedGreetingAction, String> { context ->
                    context.dispatcher.dispatch(GreetingAction(context.action.name))
                }
            }
            dispatchRoute { NestedGreetingAction("Ada") }
        }

        val response = client.get("/dispatch")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello Ada", response.bodyAsText())
    }

    @Test
    fun `routes by exact action class and rejects unsupported actions`() = testApplication {
        application {
            install(Brygadzista) {
                handler<BaseAction, String> { "base" }
            }
            routing {
                get("/base") { call.respondText(call.actionDispatcher.dispatch(BaseAction())) }
                get("/child") { call.respondText(call.actionDispatcher.dispatch(ChildAction)) }
            }
        }

        assertEquals("base", client.get("/base").bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError, client.get("/child").status)
    }

    @Test
    fun `first context factory wins and custom handlers receive its context`() = testApplication {
        application {
            install(Brygadzista) {
                contextFactory { _, action, _ ->
                    (action as? ContextAction)?.let { GreetingContext(it, "first") }
                }
                contextFactory { _, action, _ ->
                    (action as? ContextAction)?.let { GreetingContext(it, "second") }
                }
                handler<ContextAction, String, GreetingContext>(GreetingContext::class) { it.source }
            }
            dispatchRoute { ContextAction }
        }

        assertEquals("first", client.get("/dispatch").bodyAsText())
    }

    @Test
    fun `context mismatches fail before invoking the handler`() = testApplication {
        var invoked = false
        application {
            install(Brygadzista) {
                handler<ContextAction, String, GreetingContext>(GreetingContext::class) {
                    invoked = true
                    it.source
                }
            }
            dispatchRoute { ContextAction }
        }

        assertEquals(HttpStatusCode.InternalServerError, client.get("/dispatch").status)
        assertEquals(false, invoked)
    }

    @Test
    fun `interceptors wrap successful and failing handlers in registration order`() = testApplication {
        val events = mutableListOf<String>()
        application {
            install(Brygadzista) {
                interceptor(RecordingInterceptor("outer", events))
                interceptor(RecordingInterceptor("inner", events))
                handler<GreetingAction, String> {
                    events += "handler"
                    "Hello ${it.action.name}"
                }
                handler<FailingAction, String> {
                    events += "failing-handler"
                    throw IllegalStateException("failed")
                }
            }
            routing {
                get("/success") { call.respondText(call.actionDispatcher.dispatch(GreetingAction("Ada"))) }
                get("/failure") { call.respondText(call.actionDispatcher.dispatch(FailingAction)) }
            }
        }

        assertEquals(HttpStatusCode.OK, client.get("/success").status)
        assertEquals(listOf("outer-before", "inner-before", "handler", "inner-after", "outer-after"), events)

        events.clear()
        assertEquals(HttpStatusCode.InternalServerError, client.get("/failure").status)
        assertEquals(listOf("outer-before", "inner-before", "failing-handler", "inner-failure", "outer-failure"), events)
    }

    @Test
    fun `duplicate handlers fail when the plugin is installed`() {
        assertFailsWith<IllegalStateException> {
            testApplication {
                application {
                    install(Brygadzista) {
                        handler<GreetingAction, String> { "first" }
                        handler<GreetingAction, String> { "second" }
                    }
                }
                startApplication()
            }
        }
    }

    private fun Application.dispatchRoute(action: suspend () -> Action<String>) {
        routing {
            get("/dispatch") {
                call.respondText(call.actionDispatcher.dispatch(action()))
            }
        }
    }

    private data class GreetingAction(val name: String) : Action<String>

    private data class NestedGreetingAction(val name: String) : Action<String>

    private open class BaseAction : Action<String>

    private data object ChildAction : BaseAction()

    private data object ContextAction : Action<String>

    private data object FailingAction : Action<String>

    private class GreetingContext(
        override val action: ContextAction,
        val source: String,
    ) : ActionContext<ContextAction>(action)

    private class RecordingInterceptor(
        private val name: String,
        private val events: MutableList<String>,
    ) : SuspendingActionInterceptor {
        override suspend fun <R> intercept(
            context: ActionContext<Action<R>>,
            proceed: suspend () -> R,
        ): R {
            events += "$name-before"
            return try {
                proceed().also { events += "$name-after" }
            } catch (failure: IllegalStateException) {
                events += "$name-failure"
                throw failure
            }
        }
    }
}
