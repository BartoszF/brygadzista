package pl.bfelis.brygadzista.ktorexample

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ktor.Brygadzista
import pl.bfelis.brygadzista.ktor.actionDispatcher

data class GreetingAction(val name: String) : Action<String>

fun Application.module() {
    install(Brygadzista) {
        handler<GreetingAction, String> { context -> "Hello, ${context.action.name}!" }
    }

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
