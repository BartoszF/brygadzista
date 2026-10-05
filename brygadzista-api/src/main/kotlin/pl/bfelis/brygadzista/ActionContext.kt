package pl.bfelis.brygadzista

/** Carries an action and optional request-specific data to its handler. */
open class ActionContext<out A : Action<*>>(
    /** The action being handled. */
    open val action: A,
)
