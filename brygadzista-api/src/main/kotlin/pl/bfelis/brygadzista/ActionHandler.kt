package pl.bfelis.brygadzista

/**
 * Marks a Spring bean method as an action handler.
 *
 * The method must accept exactly one `ActionContext<ConcreteAction>` parameter. The concrete
 * action type is inferred from that parameter.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ActionHandler
