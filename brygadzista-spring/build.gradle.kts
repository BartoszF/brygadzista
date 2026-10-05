configureMavenPublishing(
    publicationName = "Brygadzista Spring integration",
    publicationDescription = "Spring integration for the Brygadzista action dispatcher",
)

dependencies {
    implementation(project(":brygadzista-api"))
    implementation(Dependencies.SpringBoot.autoconfigure)
}
