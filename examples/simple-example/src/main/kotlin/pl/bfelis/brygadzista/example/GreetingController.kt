package pl.bfelis.brygadzista.example

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import pl.bfelis.brygadzista.ActionDispatcher

@RestController
class GreetingController(private val dispatcher: ActionDispatcher) {
    @GetMapping("/greetings/{name}")
    fun greeting(@PathVariable name: String): Greeting =
        dispatcher.dispatch(GreetingAction(name))
}
