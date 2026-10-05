package pl.bfelis.brygadzista

/** Thrown when an action has no registered handler. */
class UnsupportedActionException(
    /** The action type that could not be dispatched. */
    actionType: Class<out Action<*>>,
) : RuntimeException("No action handler registered for ${actionType.name}")
