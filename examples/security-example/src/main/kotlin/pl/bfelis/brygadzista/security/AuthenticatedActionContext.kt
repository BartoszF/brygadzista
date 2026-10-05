package pl.bfelis.brygadzista.security

import pl.bfelis.brygadzista.ActionContext

class AuthenticatedActionContext<A : AuthorizedAction<*>>(
    override val action: A,
    val username: String,
    val role: UserRole,
) : ActionContext<A>(action)
