package pl.bfelis.brygadzista

fun interface ActionContextFactory {
    fun create(action: Action<*>): ActionContext<*>?
}
