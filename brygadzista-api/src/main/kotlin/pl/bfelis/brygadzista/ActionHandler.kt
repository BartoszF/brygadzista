package pl.bfelis.brygadzista

/**
 * Marks a Spring bean method as an action handler.
 *
 * The method must accept exactly one `ActionContext<ConcreteAction>` parameter and return a type
 * assignable to the action's declared result type. The concrete action and result types are
 * inferred from that parameter.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ActionHandler
