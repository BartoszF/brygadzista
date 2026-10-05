package pl.bfelis.brygadzista.example

import pl.bfelis.brygadzista.Action

data class GreetingAction(val name: String) : Action<Greeting>

data class Greeting(val message: String)
