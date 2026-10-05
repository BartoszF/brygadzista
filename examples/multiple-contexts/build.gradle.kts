plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":brygadzista-api"))
    implementation(project(":brygadzista-impl"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc:4.1.1")
}
