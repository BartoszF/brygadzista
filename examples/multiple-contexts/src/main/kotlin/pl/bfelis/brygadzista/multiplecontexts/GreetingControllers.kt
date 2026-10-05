package pl.bfelis.brygadzista.multiplecontexts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import pl.bfelis.brygadzista.ActionDispatcher

@RestController
class HumanController(
    private val dispatcher: ActionDispatcher,
) {
    @GetMapping("/human/greetings/{name}")
    fun greeting(@PathVariable name: String): Greeting = dispatcher.dispatch(HumanGreetingAction(name))
}

@RestController
class RobotController(
    private val dispatcher: ActionDispatcher,
) {
    @GetMapping("/robot/greetings/{name}")
    fun greeting(@PathVariable name: String): Greeting = dispatcher.dispatch(RobotGreetingAction(name))
}
