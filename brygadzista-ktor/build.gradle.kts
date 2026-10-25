configureMavenPublishing(
    publicationName = "Brygadzista Ktor integration",
    publicationDescription = "Ktor integration for the Brygadzista action dispatcher",
)

dependencies {
    api(project(":brygadzista-api"))
    api(Dependencies.Ktor.serverCore)
    testImplementation(Dependencies.Ktor.serverTestHost)
}
