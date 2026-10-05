package pl.bfelis.brygadzista

interface ActionInterceptor {
    fun <R> intercept(context: ActionContext<Action<R>>, proceed: () -> R): R
}
