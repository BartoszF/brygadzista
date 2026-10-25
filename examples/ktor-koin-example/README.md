# Ktor with Koin

This example registers a Brygadzista action handler in a Koin module and lets
`BrygadzistaKoin` install it into the Ktor action dispatcher.

Run it with:

~~~shell
./gradlew :examples:ktor-koin-example:run
~~~

Then request `http://localhost:8080/greetings/Ada`.
