package pl.bfelis.brygadzista.security

import org.springframework.stereotype.Component
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionHandler

@Component
class SecurityActionHandlers {
    @ActionHandler
    fun publicAction(context: ActionContext<PublicMessageAction>) =
        ActionResponse("Public action")

    @ActionHandler
    fun authorizedAction(context: AuthenticatedActionContext<AuthorizedMessageAction>) =
        ActionResponse("Authorized action", context.username, context.role)

    @ActionHandler
    fun adminAction(context: AuthenticatedActionContext<AdminMessageAction>) =
        ActionResponse("Admin action", context.username, context.role)
}
