package pl.bfelis.brygadzista.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(proxyBeanMethods = false)
class SecurityExampleApplication

fun main(args: Array<String>) {
    runApplication<SecurityExampleApplication>(*args)
}
