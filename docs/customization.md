# Customization

## Add request-specific context

`ActionContext` is open, so an application can carry data alongside an action:

~~~kotlin
class SecurityContext(
    override val action: GreetingAction,
    val userId: String,
) : ActionContext<GreetingAction>(action)
~~~

Register an `ActionContextFactory` Spring bean to create it for matching
actions. Return `null` for actions the factory does not handle:

~~~kotlin
@Order(0)
@Component
class SecurityContextFactory : ActionContextFactory {
    override fun create(action: Action<*>): ActionContext<*>? =
        (action as? GreetingAction)?.let {
            SecurityContext(it, currentUserId())
        }
}
~~~

Factories are considered in Spring order; the first non-null context is used.
Handlers can then declare the concrete context type:

~~~kotlin
@ActionHandler
fun handle(context: SecurityContext): Greeting =
    Greeting("Hello " + context.userId + ", " + context.action.name + "!")
~~~

If a handler requires a custom context, make sure an earlier factory actually
returns that type. A base ActionContext fallback is not compatible with a
handler that requires a more specific subclass.

## Wrap dispatches with interceptors

Implement `ActionInterceptor` as a Spring bean for auditing, timing, logging,
or other cross-cutting behavior:

~~~kotlin
@Order(0)
@Component
class AuditInterceptor : ActionInterceptor {
    override fun <R> intercept(
        context: ActionContext<Action<R>>,
        proceed: () -> R,
    ): R {
        auditStarted(context.action)
        return try {
            proceed().also { auditSucceeded(context.action, it) }
        } catch (failure: Throwable) {
            auditFailed(context.action, failure)
            throw failure
        }
    }
}
~~~

Interceptors run around handlers and nested dispatches. They can short-circuit
the call or replace its result, but should call proceed() when they are only
observing the lifecycle. Spring ordering determines nesting: lower order values
are the outer layers.

## Compose authorization

Brygadzista does not impose an authorization model. A context factory can read
Spring Security's current authentication, reject an action, and return a
context containing the authenticated user's data. Marker interfaces such as
`PublicAction`, `AuthorizedAction`, and `AdminAction` can keep that policy on the
action type while the factory owns the enforcement.

See the [security example](../examples/security-example/) for a complete Basic
authentication setup with public, authenticated, and admin-only actions.
