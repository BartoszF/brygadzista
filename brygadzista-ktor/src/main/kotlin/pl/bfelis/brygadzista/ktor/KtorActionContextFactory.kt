package pl.bfelis.brygadzista.ktor

import io.ktor.server.application.ApplicationCall
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.SuspendingActionDispatcher

/** Creates a Ktor-specific action context, or returns `null` when it does not apply. */
fun interface KtorActionContextFactory {
    suspend fun create(
        call: ApplicationCall,
        action: Action<*>,
        dispatcher: SuspendingActionDispatcher,
    ): ActionContext<*>?
}
