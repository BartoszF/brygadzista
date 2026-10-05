# Brygadzista v0

Brygadzista dispatches typed actions to methods on ordinary Spring beans.
Adding `brygadzista-impl` to a Spring Boot application creates the
`ActionDispatcher` automatically.

## Maven coordinates

Add the Spring integration module to a Maven Central consumer:

```kotlin
implementation("pl.bfelis:brygadzista-impl:<version>")
```

The public contracts are also available separately as
`pl.bfelis:brygadzista-api:<version>`; the implementation module already
depends on it.

## Releasing

Create and publish a GitHub Release for a final `vX.Y.Z` tag. GitHub Actions
runs the tests, signs both modules, and publishes version `X.Y.Z` to Maven
Central. Configure the protected `release` environment with the Central Portal
token and the in-memory PGP signing key before the first release. Published
versions are immutable; fix a failed release by creating a new version.

## Define an action

```kotlin
data class GreetingAction(val name: String) : Action<Greeting>

data class Greeting(val message: String)
```

## Handle it in a Spring bean

```kotlin
@Service
class GreetingService {
    @ActionHandler
    fun handle(context: ActionContext<GreetingAction>): Greeting =
        Greeting("Hello, ${context.action.name}!")
}
```

Handlers must be public methods with exactly one `ActionContext<ConcreteAction>`
parameter. The action type is taken from that generic parameter.

## Dispatch it

```kotlin
@RestController
class GreetingController(private val dispatcher: ActionDispatcher) {
    @GetMapping("/greetings/{name}")
    fun greeting(@PathVariable name: String): Greeting =
        dispatcher.dispatch(GreetingAction(name))
}
```

The dispatcher routes by the exact runtime class of the action. A handler can
inject `ActionDispatcher` and dispatch another action normally.

Duplicate handlers and malformed handler signatures fail during application
startup. Dispatching an action without a handler throws
`UnsupportedActionException`; exceptions raised by handlers are propagated.

## Custom contexts

`ActionContext` is open, so an application can add its own request or security
data:

```kotlin
class SecurityContext(
    override val action: GreetingAction,
    val userId: String,
) : ActionContext<GreetingAction>(action)

@Order(0)
class SecurityContextFactory : ActionContextFactory {
    override fun create(action: Action<*>): ActionContext<*>? =
        (action as? GreetingAction)?.let { SecurityContext(it, currentUserId()) }
}
```

Factories are Spring beans. They are checked in `@Order` order; the first one
that returns a context is used. Returning `null` means the factory does not
apply. If none applies, the dispatcher uses the base `ActionContext`.
Handlers may declare either `ActionContext<A>` or a concrete context subclass.

## Action lifecycle interception

Implement `ActionInterceptor` as a Spring bean to wrap every dispatch:

```kotlin
@Order(0)
class AuditInterceptor : ActionInterceptor {
    override fun <R> intercept(context: ActionContext<Action<R>>, proceed: () -> R): R {
        auditStarted(context.action)
        return try {
            proceed().also { auditSucceeded(context.action, it) }
        } catch (failure: Throwable) {
            auditFailed(context.action, failure)
            throw failure
        }
    }
}
```

Interceptors run in order around the handler and may short-circuit or replace
the result. They also apply to nested dispatches.

See the [multiple-contexts example](../examples/multiple-contexts/) for two
controllers, two context types, and two context factories sharing one service.
The [security example](../examples/security-example/) shows how a context
factory can use Spring Security authentication and action marker types to
enforce public, authenticated, and admin-only actions.

## Current boundaries

This first version is synchronous and intentionally has no built-in Spring
Security integration, authorization policy, polymorphic routing,
coroutine/reactive handlers, or custom configuration properties. Security and
auditing can be implemented by the context factories and interceptors
described above.
