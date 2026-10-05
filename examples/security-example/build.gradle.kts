plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":brygadzista-api"))
    implementation(project(":brygadzista-impl"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc:4.1.1")
    implementation("org.springframework.boot:spring-boot-starter-security:4.1.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.1.1")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test:4.1.1")
}
