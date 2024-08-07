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
    kover(project(":airflux-parser-jackson"))
    kover(project(":serialization-common"))
    kover(project(":serialization-core"))
    kover(project(":serialization-dsl"))
    kover(project(":serialization-std"))
    kover(project(":airflux-serialization-parser-json"))
}