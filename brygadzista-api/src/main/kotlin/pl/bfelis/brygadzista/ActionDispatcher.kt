package pl.bfelis.brygadzista

/** Dispatches actions to handlers registered by the application. */
interface ActionDispatcher {
    /**
     * Dispatches [action] and returns the handler result.
     *
     * Handlers are selected by the action's exact runtime class. Registered context factories
     * may provide a custom context, and interceptors wrap the handler invocation.
     *
     * @throws UnsupportedActionException if no handler is registered for the action type.
     */
    fun <R> dispatch(action: Action<R>): R
}
