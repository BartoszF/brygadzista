package pl.bfelis.brygadzista.impl

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import pl.bfelis.brygadzista.ActionContextFactory
import pl.bfelis.brygadzista.ActionDispatcher
import pl.bfelis.brygadzista.ActionInterceptor

/** Provides Brygadzista's default Spring Boot action dispatcher. */
@AutoConfiguration
@ConditionalOnMissingBean(ActionDispatcher::class)
class BrygadzistaAutoConfiguration {
    /** Creates the default dispatcher from the application's handlers and extensions. */
    @Bean
    fun actionDispatcher(
        beanFactory: org.springframework.beans.factory.ListableBeanFactory,
        contextFactories: List<ActionContextFactory>,
        interceptors: List<ActionInterceptor>,
    ): ActionDispatcher = SpringActionDispatcher(beanFactory, contextFactories, interceptors)
}
