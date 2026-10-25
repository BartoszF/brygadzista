package pl.bfelis.brygadzista

/** Dispatches actions to coroutine-aware handlers registered by the application. */
interface SuspendingActionDispatcher {
    /**
     * Dispatches [action] and returns the handler result.
     *
     * @throws UnsupportedActionException if no handler is registered for the action type.
     */
    suspend fun <R> dispatch(action: Action<R>): R
}
