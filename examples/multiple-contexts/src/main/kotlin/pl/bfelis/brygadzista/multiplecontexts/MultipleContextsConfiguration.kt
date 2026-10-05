package pl.bfelis.brygadzista.multiplecontexts

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class MultipleContextsConfiguration {
    @Bean
    fun humanActionContextFactory() = HumanActionContextFactory()

    @Bean
    fun robotActionContextFactory() = RobotActionContextFactory()
}
