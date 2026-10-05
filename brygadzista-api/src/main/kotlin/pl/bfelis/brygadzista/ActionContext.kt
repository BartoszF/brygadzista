package pl.bfelis.brygadzista

open class ActionContext<out A : Action<*>>(
    open val action: A,
)
