# Getting started

## Install

Add the Spring integration module to a Kotlin Gradle project:

~~~kotlin
dependencies {
    implementation("pl.bfelis:brygadzista-spring:<version>")
}
~~~

Find released coordinates on [Maven Central](https://central.sonatype.com/search?q=pl.bfelis%3Abrygadzista-spring).
The Spring module depends on `brygadzista-api`, so applications normally need
only this one dependency.

## Define an action

An action is a value implementing `Action<R>`, where R is the handler result:

~~~kotlin
data class GreetingAction(val name: String) : Action<Greeting>

data class Greeting(val message: String)
~~~

Actions can be data classes, objects, or any other concrete Kotlin type. Keep
the input they need on the action itself.

## Add a handler

Put `@ActionHandler` on a method of a Spring bean. The method takes exactly one
`ActionContext<ConcreteAction>` parameter; the action type in that parameter is
how Brygadzista registers the handler.

~~~kotlin
@Service
class GreetingService {
    @ActionHandler
    fun handle(context: ActionContext<GreetingAction>): Greeting =
        Greeting("Hello, " + context.action.name + "!")
}
~~~

## Dispatch the action

`brygadzista-spring` auto-configures an `ActionDispatcher` when the application
does not provide one itself:

~~~kotlin
@RestController
class GreetingController(private val dispatcher: ActionDispatcher) {
    @GetMapping("/greetings/{name}")
    fun greeting(@PathVariable name: String): Greeting =
        dispatcher.dispatch(GreetingAction(name))
}
~~~

That is the whole happy path. The [simple example](../examples/simple-example/)
contains the same flow as a runnable Spring Boot application.
