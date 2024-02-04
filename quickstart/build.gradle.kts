plugins {
    kotlin("jvm")
    java
    application

    id("detekt-convention")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val jacksonVersion by extra { "2.13.3" }

dependencies {
    implementation(project(":airflux-serialization-core"))
    implementation(project(":airflux-serialization-dsl"))
    implementation(project(":airflux-serialization-std"))
    implementation(project(":airflux-parser-jackson"))

    /* Kotlin */
    implementation(kotlin("stdlib"))

    /* Jackson */
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    java {
        sourceCompatibility = Configuration.JVM.compatibility
        targetCompatibility = Configuration.JVM.compatibility
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = Configuration.JVM.targetVersion
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
