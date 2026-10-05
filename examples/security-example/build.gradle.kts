plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":brygadzista-api"))
    implementation(project(":brygadzista-spring"))
    implementation(Dependencies.SpringBoot.starterWebMvc)
    implementation(Dependencies.SpringBoot.starterSecurity)
    testImplementation(Dependencies.SpringBoot.starterTest)
    testImplementation(Dependencies.SpringBoot.starterWebMvcTest)
}
