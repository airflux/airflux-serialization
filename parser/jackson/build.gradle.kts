import Configuration.JVM
import Configuration.Publishing.mavenCentralMetadata
import Configuration.Publishing.mavenSonatypeRepository
import Configuration.Versions
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.api.KoverTaskExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("detekt-convention")
    id("publishing-convention")
    `java-library`

    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))

    implementation(project(":airflux-serialization-core"))

    /* JSON */
    implementation("com.fasterxml.jackson.core:jackson-core:${Versions.Jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.Jackson}") {
        exclude(group = "org.jetbrains.kotlin")
    }

    /* Kotest */
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.Test.Kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.Test.Kotest}")

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
            includes.addAll("io.github.airflux.parser.*")
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
