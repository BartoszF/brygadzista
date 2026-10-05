package pl.bfelis.brygadzista.impl

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import pl.bfelis.brygadzista.ActionDispatcher

@AutoConfiguration
@ConditionalOnMissingBean(ActionDispatcher::class)
class BrygadzistaAutoConfiguration {
    @Bean
    fun actionDispatcher(beanFactory: org.springframework.beans.factory.ListableBeanFactory): ActionDispatcher =
        SpringActionDispatcher(beanFactory)
}
