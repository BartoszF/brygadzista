package pl.bfelis.brygadzista

/** Wraps action dispatches with cross-cutting behavior such as auditing or authorization. */
interface ActionInterceptor {
    /**
     * Intercepts a dispatch represented by [context].
     *
     * Call [proceed] to invoke the next interceptor or handler. An interceptor may short-circuit
     * the dispatch or replace its result.
     */
    fun <R> intercept(context: ActionContext<Action<R>>, proceed: () -> R): R
}
