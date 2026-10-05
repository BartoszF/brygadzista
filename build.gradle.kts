import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    kotlin("jvm") version "2.4.10" apply false
    id("org.springframework.boot") version "4.1.1" apply false
    id("com.gradleup.nmcp.aggregation") version "1.6.2"
}

group = "pl.bfelis"
version = System.getenv("RELEASE_VERSION") ?: "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

nmcpAggregation {
    centralPortal {
        username = System.getenv("MAVEN_CENTRAL_PORTAL_USERNAME") ?: ""
        password = System.getenv("MAVEN_CENTRAL_PORTAL_PASSWORD") ?: ""
        publishingType = "AUTOMATIC"
        publicationName = "brygadzista:${System.getenv("RELEASE_VERSION") ?: "local"}"
    }
}

dependencies {
    nmcpAggregation(project(":brygadzista-api"))
    nmcpAggregation(project(":brygadzista-impl"))
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-test:2.3.0")

    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(25)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
