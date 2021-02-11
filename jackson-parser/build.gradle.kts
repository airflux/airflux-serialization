plugins {
    kotlin("jvm") version "1.4.30"
    `java-library`
}

group = "io.github.airflux"
version = "0.0.1-SNAPSHOT"

val jvmTargetVersion by extra { "1.8" }
val jacksonVersion by extra { "2.12.1" }

repositories {
    jcenter()
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.github.airflux:airflux-core:0.0.1-SNAPSHOT")

    /* JSON */
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = jvmTargetVersion
                suppressWarnings = false
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
}
