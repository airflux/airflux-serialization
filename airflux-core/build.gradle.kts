import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.github.airflux.gradle.AirfluxPublishingPlugin.Companion.mavenCentralMetadata
import io.github.airflux.gradle.AirfluxPublishingPlugin.Companion.mavenSonatypeRepository
import io.github.airflux.gradle.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("airflux-configuration-plugin")
    id("airflux-jacoco-plugin")
    id("airflux-pitest-plugin")
    id("airflux-detekt-plugin")
    id("airflux-publishing-plugin")
    `java-library`
}

val jvmTargetVersion by extra { "1.8" }

repositories {
    mavenCentral()
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    /* Test */
    testImplementation(kotlin("test-junit5"))

    /* Junit */
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.platform:junit-platform-engine:${Versions.JUnit.Platform}")
    testImplementation("org.junit.platform:junit-platform-launcher:${Versions.JUnit.Platform}")

    /* PITest */
    testImplementation("org.pitest:pitest-junit5-plugin:${Versions.PiTest.JUnit5}")
}

tasks {
    java {
        withJavadocJar()
        withSourcesJar()
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = jvmTargetVersion
                suppressWarnings = false
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all"
                )
            }
        }

    detekt {
        source = project.files("src/main/kotlin", "src/test/kotlin")
    }
}

configure<PitestPluginExtension> {
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
}

val mavenPublicationName = "JVM"
publishing {
    publications {
        create<MavenPublication>(mavenPublicationName) {
            from(components["java"])
            pom(mavenCentralMetadata)
        }
    }

    repositories {
        mavenLocal()
        mavenSonatypeRepository(project)
    }
}


signing {
    sign(publishing.publications[mavenPublicationName])
}
