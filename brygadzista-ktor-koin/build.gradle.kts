configureMavenPublishing(
    publicationName = "Brygadzista Ktor Koin integration",
    publicationDescription = "Koin integration for the Brygadzista Ktor action dispatcher",
)

dependencies {
    api(project(":brygadzista-ktor"))
    api(Dependencies.Koin.ktor)
    testImplementation(Dependencies.Ktor.serverTestHost)
}
