plugins {
    id("org.danilopianini.publish-on-central") version "9.2.12"
}

publishOnCentral {
    repoOwner.set("Bartosz Felis")
    projectDescription.set("Spring integration for the Brygadzista action dispatcher")
    projectLongName.set("Brygadzista Spring integration")
    licenseName.set("MIT License")
    licenseUrl.set("https://opensource.org/license/mit")
    projectUrl.set("https://github.com/BartoszF/brygadzista")
    scmConnection.set("scm:git:https://github.com/BartoszF/brygadzista.git")
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            developers {
                developer {
                    name.set("Bartosz Felis")
                    email.set("felis.bartosz@gmail.com")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        providers.environmentVariable("SIGNING_KEY").orNull,
        providers.environmentVariable("SIGNING_PASSWORD").orNull,
    )
}

dependencies {
    implementation(project(":brygadzista-api"))
    implementation("org.springframework.boot:spring-boot-autoconfigure:4.1.1")
}
