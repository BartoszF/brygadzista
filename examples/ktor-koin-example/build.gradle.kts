plugins {
    application
}

dependencies {
    implementation(project(":brygadzista-ktor-koin"))
    implementation(Dependencies.Ktor.serverNetty)
}

application {
    mainClass = "pl.bfelis.brygadzista.ktorkoinexample.KtorKoinExampleApplicationKt"
}
