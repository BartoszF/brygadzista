plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "brygadzista"

include(
    ":brygadzista-api",
    ":brygadzista-ktor",
    ":brygadzista-ktor-koin",
    ":brygadzista-spring",
    ":examples:ktor-koin-example",
    ":examples:ktor-example",
    ":examples:simple-example",
    ":examples:multiple-contexts",
    ":examples:security-example",
)
