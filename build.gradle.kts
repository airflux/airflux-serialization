plugins {
    id("kover-merge-convention")
    id("licenses-convention")
}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1-SNAPSHOT"
    group = "io.github.airflux"
}

dependencies{
    kover(project(":airflux-serialization-common"))
    kover(project(":airflux-serialization-core"))
    kover(project(":airflux-serialization-dsl"))
    kover(project(":airflux-serialization-std"))
    kover(project(":airflux-serialization-parser-jackson"))
    kover(project(":airflux-serialization-parser-json"))
}
