object Dependencies {
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlinTest}"

    object SpringBoot {
        const val autoconfigure = "org.springframework.boot:spring-boot-autoconfigure:${Versions.springBoot}"
        const val starterSecurity = "org.springframework.boot:spring-boot-starter-security:${Versions.springBoot}"
        const val starterTest = "org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}"
        const val starterWebMvc = "org.springframework.boot:spring-boot-starter-webmvc:${Versions.springBoot}"
        const val starterWebMvcTest = "org.springframework.boot:spring-boot-starter-webmvc-test:${Versions.springBoot}"
    }
}
