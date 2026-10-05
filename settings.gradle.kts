plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradleup.nmcp.settings") version "1.6.2"
}

nmcpSettings {
    centralPortal {
        username = System.getenv("MAVEN_CENTRAL_PORTAL_USERNAME") ?: ""
        password = System.getenv("MAVEN_CENTRAL_PORTAL_PASSWORD") ?: ""
        publishingType = "AUTOMATIC"
        publicationName = "brygadzista:${System.getenv("RELEASE_VERSION") ?: "local"}"
    }
}

rootProject.name = "brygadzista"

include(
    ":brygadzista-api",
    ":brygadzista-impl",
    ":examples:simple-example",
    ":examples:multiple-contexts",
    ":examples:security-example",
)
