package pl.bfelis.brygadzista.ktorkoinexample

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.dsl.module
import org.koin.core.qualifier.named
import org.koin.ktor.plugin.KoinIsolated
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ktor.KtorActionHandler
import pl.bfelis.brygadzista.ktor.actionDispatcher
import pl.bfelis.brygadzista.ktor.ktorActionHandler
import pl.bfelis.brygadzista.ktor.koin.BrygadzistaKoin

data class GreetingAction(val name: String) : Action<String>

private class GreetingService {
    fun greet(name: String) = "Hello, $name!"
}

private val actions =
    module {
        single { GreetingService() }
        single<KtorActionHandler>(named("greeting")) {
            val service = get<GreetingService>()
            ktorActionHandler<GreetingAction, String> { service.greet(it.action.name) }
        }
    }

fun Application.module() {
    install(KoinIsolated) {
        modules(actions)
    }
    install(BrygadzistaKoin)

    routing {
        get("/greetings/{name}") {
            val name = call.parameters["name"] ?: return@get call.respondText("", status = HttpStatusCode.BadRequest) { }
            call.respondText(call.actionDispatcher.dispatch(GreetingAction(name)))
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
