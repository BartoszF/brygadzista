package pl.bfelis.brygadzista.security

import pl.bfelis.brygadzista.Action

interface PublicAction<out R> : Action<R>

interface AuthorizedAction<out R> : Action<R>

interface AdminAction<out R> : AuthorizedAction<R>

enum class UserRole {
    USER,
    ADMIN,
    ;

    val authority: String get() = "ROLE_$name"
}

data class ActionResponse(
    val message: String,
    val username: String? = null,
    val role: UserRole? = null,
)

data object PublicMessageAction : PublicAction<ActionResponse>

data object AuthorizedMessageAction : AuthorizedAction<ActionResponse>

data object AdminMessageAction : AdminAction<ActionResponse>
