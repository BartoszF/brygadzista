package pl.bfelis.brygadzista.security

import org.springframework.core.annotation.Order
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionContextFactory

@Order(0)
@Component
class SecurityActionContextFactory : ActionContextFactory {
    override fun create(action: Action<*>): ActionContext<*>? =
        (action as? AuthorizedAction<*>)?.let { authenticatedContext(it) }

    private fun authenticatedContext(action: AuthorizedAction<*>): ActionContext<*> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || authentication is AnonymousAuthenticationToken || !authentication.isAuthenticated) {
            throw AuthenticationCredentialsNotFoundException("An authenticated user is required")
        }

        val role = UserRole.entries.singleOrNull { candidate ->
            authentication.authorities.any { authority -> authority.authority == candidate.authority }
        } ?: throw AccessDeniedException("The authenticated user has no supported role")

        if (action is AdminAction<*> && role != UserRole.ADMIN) {
            throw AccessDeniedException("The ADMIN role is required")
        }

        return AuthenticatedActionContext(action, authentication.name, role)
    }
}
