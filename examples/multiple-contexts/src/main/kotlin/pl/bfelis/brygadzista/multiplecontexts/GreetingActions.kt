package pl.bfelis.brygadzista.multiplecontexts

import org.springframework.stereotype.Component
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionHandler

data class Greeting(
    val message: String,
)

data class HumanGreetingAction(
    val name: String,
) : Action<Greeting> {
    @Component
    class Handler(
        private val service: GreetingService,
    ) {
        @ActionHandler
        fun handle(context: HumanActionContext): Greeting = service.greet(context.action.name, "human")
    }
}

data class RobotGreetingAction(
    val name: String,
) : Action<Greeting> {
    @Component
    class Handler(
        private val service: GreetingService,
    ) {
        @ActionHandler
        fun handle(context: RobotActionContext): Greeting = service.greet(context.action.name, "robot")
    }
}
