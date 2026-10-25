package pl.bfelis.brygadzista.ktor.koin

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
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
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.KoinIsolated
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.SuspendingActionDispatcher
import pl.bfelis.brygadzista.SuspendingActionInterceptor
import pl.bfelis.brygadzista.ktor.KtorActionContextFactory
import pl.bfelis.brygadzista.ktor.KtorActionHandler
import pl.bfelis.brygadzista.ktor.actionDispatcher
import pl.bfelis.brygadzista.ktor.contextualKtorActionHandler
import pl.bfelis.brygadzista.ktor.ktorActionHandler

class BrygadzistaKoinTest {
    @Test
    fun `imports Koin handlers context factories and interceptors`() = testApplication {
        val events = mutableListOf<String>()
        application {
            install(KoinIsolated) {
                modules(actionModule(events))
            }
            install(BrygadzistaKoin)
            routing {
                get("/greeting") {
                    call.respondText(call.actionDispatcher.dispatch(GreetingAction("Ada")))
                }
                get("/context") {
                    call.respondText(call.actionDispatcher.dispatch(ContextAction))
                }
            }
        }

        assertEquals("Hello Ada", client.get("/greeting").bodyAsText())
        assertEquals("Koin context", client.get("/context").bodyAsText())
        assertEquals(
            listOf(
                "first-before",
                "second-before",
                "second-after",
                "first-after",
                "first-before",
                "second-before",
                "second-after",
                "first-after",
            ),
            events,
        )
    }

    @Test
    fun `rejects duplicate Koin handlers during startup`() {
        assertFailsWith<IllegalStateException> {
            testApplication {
                application {
                    install(KoinIsolated) {
                        modules(
                            module {
                                single<KtorActionHandler>(named("first")) { ktorActionHandler<GreetingAction, String> { "first" } }
                                single<KtorActionHandler>(named("second")) { ktorActionHandler<GreetingAction, String> { "second" } }
                            },
                        )
                    }
                    install(BrygadzistaKoin)
                }
                startApplication()
            }
        }
    }

    private fun actionModule(events: MutableList<String>): Module =
        module {
            single { GreetingService() }
            single<KtorActionHandler>(named("greeting")) {
                val service = get<GreetingService>()
                ktorActionHandler<GreetingAction, String> { service.greet(it.action.name) }
            }
            single<KtorActionContextFactory> {
                KtorActionContextFactory { _, action, _ ->
                    (action as? ContextAction)?.let { GreetingContext(it, "Koin") }
                }
            }
            single<KtorActionHandler>(named("context")) {
                contextualKtorActionHandler<ContextAction, String, GreetingContext> { "${it.source} context" }
            }
            single<SuspendingActionInterceptor>(named("first")) { RecordingInterceptor("first", events) }
            single<SuspendingActionInterceptor>(named("second")) { RecordingInterceptor("second", events) }
        }

    private data class GreetingAction(val name: String) : Action<String>

    private data object ContextAction : Action<String>

    private class GreetingService {
        fun greet(name: String) = "Hello $name"
    }

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
            return proceed().also { events += "$name-after" }
        }
    }
}
