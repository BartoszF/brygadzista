import org.gradle.api.publish.maven.MavenPublication

plugins {
    `maven-publish`
    signing
    id("com.gradleup.nmcp")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("Brygadzista API")
                description.set("Public contracts and annotations for Brygadzista")
                url.set("https://github.com/BartoszF/brygadzista")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }
                developers {
                    developer {
                        name.set("Bartosz Felis")
                        email.set("felis.bartosz@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/BartoszF/brygadzista.git")
                    developerConnection.set("scm:git:ssh://git@github.com/BartoszF/brygadzista.git")
                    url.set("https://github.com/BartoszF/brygadzista")
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
    sign(publishing.publications["mavenJava"])
}
