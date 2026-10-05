import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension

fun Project.configureMavenPublishing(
    publicationName: String,
    publicationDescription: String,
) {
    pluginManager.apply("maven-publish")
    pluginManager.apply("signing")
    pluginManager.apply("com.gradleup.nmcp")

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components.getByName("java"))
                pom {
                    name.set(publicationName)
                    description.set(publicationDescription)
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

    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(
            providers.environmentVariable("SIGNING_KEY").orNull,
            providers.environmentVariable("SIGNING_PASSWORD").orNull,
        )
        sign(this@configureMavenPublishing.extensions.getByType<PublishingExtension>().publications.getByName("mavenJava"))
    }
}
