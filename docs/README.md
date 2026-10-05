# Brygadzista v0

Brygadzista dispatches typed actions to methods on ordinary Spring beans.
Adding `brygadzista-impl` to a Spring Boot application creates the
`ActionDispatcher` automatically.

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

## v0 boundaries

This first version is synchronous and intentionally has no Spring Security
integration, authorization, auditing, lifecycle hooks, polymorphic routing,
coroutine/reactive handlers, or custom configuration properties.
