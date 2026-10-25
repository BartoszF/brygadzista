# Brygadzista

*/brɨɡadʑista/* — Polish for “foreman”. A small Kotlin library that dispatches
typed actions to their handlers in Spring Boot and Ktor applications.

Define an action, annotate one handler, and inject `ActionDispatcher` wherever
the action should run. Brygadzista handles the wiring; your application keeps
the actual work.

## Install

Add the Spring integration module from [Maven Central](https://central.sonatype.com/search?q=pl.bfelis%3Abrygadzista-spring):

~~~kotlin
dependencies {
    implementation("pl.bfelis:brygadzista-spring:<version>")
}
~~~

The Spring module brings in the public API module transitively. Use
`pl.bfelis:brygadzista-api:<version>` directly only when you need the contracts
without an integration. For Ktor applications, use:

~~~kotlin
dependencies {
    implementation("pl.bfelis:brygadzista-ktor:<version>")
}
~~~

## Use

~~~kotlin
data class GreetingAction(val name: String) : Action<Greeting>
data class Greeting(val message: String)

@Service
class GreetingService {
    @ActionHandler
    fun handle(context: ActionContext<GreetingAction>) =
        Greeting("Hello, " + context.action.name + "!")
}

@RestController
class GreetingController(private val dispatcher: ActionDispatcher) {
    @GetMapping("/greetings/{name}")
    fun greeting(@PathVariable name: String) =
        dispatcher.dispatch(GreetingAction(name))
}
~~~

Spring Boot discovers the handler and creates `ActionDispatcher` automatically.
Handlers are selected by the action's exact runtime class.

Ktor applications install `Brygadzista`, register suspending handlers explicitly,
and dispatch through `call.actionDispatcher`. See the [Ktor guide](docs/ktor.md).

Read the [documentation index](docs/README.md) for the dispatch lifecycle and
extension points, or run the [examples](examples/README.md).
