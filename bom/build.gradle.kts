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
        api(project(":airflux-serialization"))
        api(project(":airflux-jackson-parser"))
    }
}

val mavenPublicationName = "mavenBom"
publishing {
    publications {
        create<MavenPublication>(mavenPublicationName) {
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
