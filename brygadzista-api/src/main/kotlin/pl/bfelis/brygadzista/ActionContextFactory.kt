package pl.bfelis.brygadzista

/** Creates a custom context for an action when the factory applies. */
fun interface ActionContextFactory {
    /**
     * Creates a context for [action].
     *
     * @return a context to use, or `null` when this factory does not apply.
     */
    fun create(action: Action<*>): ActionContext<*>?
}
