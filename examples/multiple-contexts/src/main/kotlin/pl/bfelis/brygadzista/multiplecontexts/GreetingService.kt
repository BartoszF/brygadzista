package pl.bfelis.brygadzista.multiplecontexts

import org.springframework.stereotype.Service

@Service
class GreetingService {
    fun greet(
        name: String,
        origin: String,
    ) = Greeting("Hello, $name from $origin!")
}
