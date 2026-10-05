package pl.bfelis.brygadzista.multiplecontexts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(proxyBeanMethods = false)
class MultipleContextsApplication

fun main(args: Array<String>) {
    runApplication<MultipleContextsApplication>(*args)
}
