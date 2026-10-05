package pl.bfelis.brygadzista.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.stereotype.Service
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionDispatcher
import pl.bfelis.brygadzista.ActionHandler

@SpringBootApplication(proxyBeanMethods = false)
class ExampleApplication

fun main(args: Array<String>) {
    runApplication<ExampleApplication>(*args)
}

data class GreetingAction(val name: String) : Action<Greeting>

data class Greeting(val message: String)

@Service
class GreetingService {
    @ActionHandler
    fun handle(context: ActionContext<GreetingAction>): Greeting =
        Greeting("Hello, ${context.action.name}!")
}

@RestController
class GreetingController(private val dispatcher: ActionDispatcher) {
    @GetMapping("/greetings/{name}")
    fun greeting(@PathVariable name: String): Greeting =
        dispatcher.dispatch(GreetingAction(name))
}
