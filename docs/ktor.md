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

## Koin

Add the optional Koin adapter when handlers and extensions are defined in Koin:

~~~kotlin
dependencies {
    implementation("pl.bfelis:brygadzista-ktor-koin:<version>")
}
~~~

Bind typed handler descriptors in a Koin module. The descriptor keeps the
action and context types, while Koin supplies the handler's collaborators.
Give each handler, factory, or interceptor of the same type a distinct Koin
qualifier so Koin can retain every binding.

~~~kotlin
val actions = module {
    single { GreetingService() }
    single<KtorActionHandler>(named("greeting")) {
        val service = get<GreetingService>()
        ktorActionHandler<GreetingAction, Greeting> { context ->
            service.greet(context.action.name)
        }
    }
}
~~~

Install Koin before the Brygadzista Koin plugin. `KoinIsolated` is recommended
because the adapter reads Koin from the current Ktor application rather than
the global context.

~~~kotlin
fun Application.module() {
    install(KoinIsolated) { modules(actions) }
    install(BrygadzistaKoin)
}
~~~

The adapter also imports every `KtorActionContextFactory` and
`SuspendingActionInterceptor` bound in Koin. Their order is the order Koin
returns: factories are tried first-to-last and interceptors are outermost-to-
innermost.

Use `contextualKtorActionHandler<Action, Result, Context>` for a handler that
requires a custom action context. See the runnable [Ktor Koin example](../examples/ktor-koin-example/).
