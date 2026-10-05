package pl.bfelis.brygadzista

interface ActionDispatcher {
    fun <R> dispatch(action: Action<R>): R
}
