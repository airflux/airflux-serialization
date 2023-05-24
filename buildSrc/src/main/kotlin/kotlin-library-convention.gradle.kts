import Configuration.Publishing.mavenCentralMetadata
import Configuration.Publishing.mavenSonatypeRepository

plugins {
    id("kotlin-common-convention")
    `java-library`

    id("publishing-convention")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

tasks {

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withJavadocJar()
        withSourcesJar()
    }
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
