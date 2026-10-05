package pl.bfelis.brygadzista.security

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pl.bfelis.brygadzista.ActionDispatcher

@RestController
class SecurityController(
    private val dispatcher: ActionDispatcher,
) {
    @GetMapping("/public")
    fun publicAction() = dispatcher.dispatch(PublicMessageAction)

    @GetMapping("/authorized")
    fun authorizedAction() = dispatcher.dispatch(AuthorizedMessageAction)

    @GetMapping("/admin")
    fun adminAction() = dispatcher.dispatch(AdminMessageAction)
}
