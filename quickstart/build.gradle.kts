plugins {
    kotlin("jvm") version "1.5.30"
    java
    application
}

repositories {
    mavenCentral()
    mavenLocal()
}

val jvmTargetVersion by extra { "1.8" }
val jacksonVersion by extra { "2.12.4" }

dependencies {
    implementation("io.github.airflux:airflux-core:0.0.1-SNAPSHOT")
    implementation("io.github.airflux:airflux-dsl:0.0.1-SNAPSHOT")
    implementation("io.github.airflux:airflux-jackson-parser:0.0.1-SNAPSHOT")

    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    /* Jackson */
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
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all"
                )
            }
        }

    val javaMainClass = "io.github.airflux.quickstart.QuickstartKt"

    application {
        mainClass.set(javaMainClass)
    }

    jar {
        manifest {
            attributes["Main-Class"] = javaMainClass
        }

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map {
                    if (it.isDirectory) it else zipTree(it)
                }
        })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
