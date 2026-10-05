package pl.bfelis.brygadzista

data class ActionContext<out A : Action<*>>(val action: A)
