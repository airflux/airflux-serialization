plugins {
    kotlin("jvm")
}

val jacksonVersion by extra { "2.12.1" }

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":airflux-core"))

    /* JSON */
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
}
