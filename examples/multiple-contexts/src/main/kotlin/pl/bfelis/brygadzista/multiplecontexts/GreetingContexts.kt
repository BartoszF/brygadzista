package pl.bfelis.brygadzista.multiplecontexts

import org.springframework.core.annotation.Order
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionContextFactory

class HumanActionContext(
    override val action: HumanGreetingAction,
) : ActionContext<HumanGreetingAction>(action)

class RobotActionContext(
    override val action: RobotGreetingAction,
) : ActionContext<RobotGreetingAction>(action)

@Order(0)
class HumanActionContextFactory : ActionContextFactory {
    override fun create(action: Action<*>): ActionContext<*>? =
        (action as? HumanGreetingAction)?.let(::HumanActionContext)
}

@Order(1)
class RobotActionContextFactory : ActionContextFactory {
    override fun create(action: Action<*>): ActionContext<*>? =
        (action as? RobotGreetingAction)?.let(::RobotActionContext)
}
