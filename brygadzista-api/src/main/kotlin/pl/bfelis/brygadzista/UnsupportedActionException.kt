package pl.bfelis.brygadzista

class UnsupportedActionException(
    actionType: Class<out Action<*>>,
) : RuntimeException("No action handler registered for ${actionType.name}")
