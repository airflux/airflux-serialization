import Configuration.Publishing.mavenCentralMetadata
import Configuration.Publishing.mavenSonatypeRepository

plugins {
    id("kotlin-common-convention")
    id("java-convention")

    id("publishing-convention")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

tasks {

    java {
        withJavadocJar()
        withSourcesJar()
    }
}

val mavenPublicationName: String = "Jvm"
publishing {
    publications {
        create<MavenPublication>(mavenPublicationName) {
            artifactId = project.name + Configuration.Artifact.jdk
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
