# Ktor

## Install

Add the Ktor integration to a Kotlin JVM project:

~~~kotlin
dependencies {
    implementation("pl.bfelis:brygadzista-ktor:<version>")
}
~~~

The module depends on `brygadzista-api` and supports Ktor 3.6.

## Configure handlers

Install `Brygadzista` once and register each handler explicitly. Ktor does not
discover handlers from a DI container.

~~~kotlin
fun Application.module() {
    install(Brygadzista) {
        handler<GreetingAction, Greeting> { context ->
            Greeting("Hello, ${context.action.name}!")
        }
    }
}
~~~

Handlers receive `KtorActionContext`, which contains the action, the current
`ApplicationCall`, and a `SuspendingActionDispatcher` for nested actions.

## Dispatch from a route

Use the dispatcher bound to the current call:

~~~kotlin
routing {
    get("/greetings/{name}") {
        val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        call.respond(call.actionDispatcher.dispatch(GreetingAction(name)))
    }
}
~~~

Handlers are selected by the action's exact runtime class. Dispatching an
unregistered action throws `UnsupportedActionException`.

## Context and interception

Register `contextFactory` entries to create a typed `ActionContext` subtype from
the current call and action. The first non-null result wins. A handler that
requires that subtype declares it with
`handler<MyAction, Result, ActionContextType>(ActionContextType::class) { ... }`.
If a factory returns an incompatible context, dispatch fails before the handler
runs.

Register `interceptor` entries for auditing, authorization, or error handling.
They wrap handlers in registration order: the first interceptor is the outermost.
Both factories and interceptors are suspending, so they can use normal Ktor
coroutine APIs.

See the runnable [Ktor example](../examples/ktor-example/).
