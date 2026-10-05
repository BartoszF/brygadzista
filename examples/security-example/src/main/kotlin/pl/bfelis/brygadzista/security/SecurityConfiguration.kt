package pl.bfelis.brygadzista.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { authorize -> authorize.anyRequest().permitAll() }
            .httpBasic(Customizer.withDefaults())
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService =
        InMemoryUserDetailsManager(
            User.withUsername("user")
                .password(passwordEncoder.encode("user"))
                .roles(UserRole.USER.name)
                .build(),
            User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(UserRole.ADMIN.name)
                .build(),
        )
}
