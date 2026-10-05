package pl.bfelis.brygadzista.example

import org.springframework.stereotype.Service
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionHandler

@Service
class GreetingService {
    @ActionHandler
    fun handle(context: ActionContext<GreetingAction>): Greeting =
        Greeting("Hello, ${context.action.name}!")
}
