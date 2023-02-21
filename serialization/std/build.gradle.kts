import Configuration.JVM
import Configuration.Publishing.mavenCentralMetadata
import Configuration.Publishing.mavenSonatypeRepository
import Configuration.Versions
import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.api.KoverTaskExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("pitest-convention")
    id("detekt-convention")
    id("publishing-convention")
    `java-library`

    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.0"

    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":airflux-serialization-core"))
    implementation(project(":airflux-serialization-dsl"))

    /* Kotlin */
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    /* Test */

    /* Kotest */
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.Test.Kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.Test.Kotest}")
    testImplementation("io.kotest:kotest-framework-datatest:${Versions.Test.Kotest}")

    /* PITest */
    testImplementation("org.pitest:pitest-junit5-plugin:${Versions.Test.PiTest.JUnit5}")
}

tasks {
    java {
        withJavadocJar()
        withSourcesJar()
    }

    withType<Test> {
        useJUnitPlatform()
        extensions.configure(KoverTaskExtension::class) {
            includes.addAll("io.github.airflux.serialization.std.*")
        }
    }

    withType<KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = JVM.targetVersion
                suppressWarnings = false
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xexplicit-api=strict"
                )
            }
        }
}

configure<DetektExtension> {
    source = project.files("src/main/kotlin", "src/test/kotlin")
}

configure<PitestPluginExtension> {
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
}

val mavenPublicationName: String = "Jvm"
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
