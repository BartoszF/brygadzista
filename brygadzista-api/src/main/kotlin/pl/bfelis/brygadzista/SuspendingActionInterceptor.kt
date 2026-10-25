package pl.bfelis.brygadzista

/** Wraps suspending action dispatches with cross-cutting behavior. */
interface SuspendingActionInterceptor {
    /** Intercepts a dispatch and may invoke, short-circuit, or replace [proceed]. */
    suspend fun <R> intercept(
        context: ActionContext<Action<R>>,
        proceed: suspend () -> R,
    ): R
}
