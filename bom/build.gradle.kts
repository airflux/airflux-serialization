import Configuration.Publishing.mavenCentralMetadata
import Configuration.Publishing.mavenSonatypeRepository

plugins {
    id("publishing-convention")
    `java-platform`
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        api(project(":airflux-serialization-core"))
        api(project(":airflux-serialization-dsl"))
        api(project(":airflux-serialization-std"))
        api(project(":airflux-jackson-parser"))
    }
}

val mavenPublicationName = "mavenBom"
publishing {
    publications {
        create<MavenPublication>(mavenPublicationName) {
            artifactId = project.name + Configuration.Artifact.jdk
            from(components["javaPlatform"])
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
