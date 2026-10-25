plugins {
    application
}

dependencies {
    implementation(project(":brygadzista-api"))
    implementation(project(":brygadzista-ktor"))
    implementation(Dependencies.Ktor.serverNetty)
    testImplementation(Dependencies.Ktor.serverTestHost)
}

application {
    mainClass = "pl.bfelis.brygadzista.ktorexample.KtorExampleApplicationKt"
}
