plugins {
    kotlin("jvm")
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
}
