import Configuration.JVM
import Configuration.Publishing.mavenCentralMetadata
import Configuration.Publishing.mavenSonatypeRepository
import Configuration.Versions
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("detekt-convention")
    id("publishing-convention")
    `java-library`
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":airflux-core"))

    /* JSON */
    implementation("com.fasterxml.jackson.core:jackson-core:${Versions.Jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.Jackson}") {
        exclude(group = "org.jetbrains.kotlin")
    }

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
                jvmTarget = JVM.targetVersion
                suppressWarnings = false
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all"
                )
            }
        }
}

configure<DetektExtension> {
    source = project.files("src/main/kotlin", "src/test/kotlin")
}

val mavenPublicationName = "Jvm"
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
