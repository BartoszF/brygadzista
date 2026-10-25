package pl.bfelis.brygadzista.ktor

import io.ktor.server.application.ApplicationCall
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.SuspendingActionDispatcher

/** Default action context for Ktor handlers. */
class KtorActionContext<A : Action<*>>(
    override val action: A,
    val call: ApplicationCall,
    val dispatcher: SuspendingActionDispatcher,
) : ActionContext<A>(action)
