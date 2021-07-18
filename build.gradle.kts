import info.solidsoft.gradle.pitest.PitestPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.6.0")
    }
}

plugins {
    kotlin("jvm") version "1.5.20"

    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    jacoco

    `maven-publish`
    signing
}

apply<info.solidsoft.gradle.pitest.PitestAggregatorPlugin>()

val jvmTargetVersion by extra { "1.8" }

val detectConfigPath = "$projectDir/config/detekt/detekt.yml"
val isSnapshot = AIRFLUX_VERSION.contains("SNAPSHOT")

allprojects {
    repositories {
        mavenCentral()
    }
}

val testReport = tasks.register<TestReport>("testReport") {
    destinationDir = file("$buildDir/reports/tests/test")
    val testTasks = subprojects.mapNotNull { it.tasks.findByName("test") }
    reportOn(testTasks)
}

subprojects {
    version = AIRFLUX_VERSION
    group = "io.github.airflux"

    apply<KotlinPlatformJvmPlugin>()
    apply<JavaLibraryPlugin>()

    apply<PitestPlugin>()
    apply<DetektPlugin>()
    apply<JacocoPlugin>()

    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    tasks {

        withType<Test> {
            useJUnitPlatform()
            finalizedBy(testReport)
        }

        withType<KotlinCompile>()
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
    }

    detekt {
        toolVersion = "1.17.1"
        ignoreFailures = true

        input = project.files("src/main/kotlin", "src/test/kotlin")
        config = project.files(detectConfigPath)
        debug = false

        reports {
            html {
                enabled = true
            }

            xml {
                enabled = true
            }
            txt {
                enabled = false
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    name.set("Airflux")
                    description.set("The library to parse, validate and generate data in the JSON (JavaScript Object Notation) format.")
                    url.set("https://airflux.github.io/airflux/")
                    licenses {
                        license {
                            name.set("Mozilla Public License Version 2.0")
                            url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                        }
                    }
                    developers {
                        developer {
                            id.set("maxim-sambulat")
                            name.set("Maxim Sambulat")
                            email.set("airflux.github.io@gmail.com")
                            organization.set("airflux")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/airflux/airflux.git")
                        developerConnection.set("scm:git:ssh://github.com:airflux/airflux.git")
                        url.set("https://github.com/airflux/airflux/tree/main")
                    }
                }
            }
        }

        repositories {
            mavenLocal()

            maven {
                name = "sonatype"
                url = if (version.toString().endsWith("SNAPSHOT"))
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            System.getenv("GPG_PRIVATE_KEY"),
            System.getenv("GPG_PRIVATE_PASSWORD")
        )

        sign(publishing.publications["mavenJava"])
    }
}

tasks {
    val jacocoReport = register<JacocoReport>("jacocoReport") {
        subprojects {
            val subproject = this

            subproject.extensions.findByType<JacocoPluginExtension>()
                ?.apply {
                    toolVersion = "0.8.7"
                }

            subproject.plugins.withType<JacocoPlugin>()
                .configureEach {
                    subproject.tasks
                        .matching { it.extensions.findByType<JacocoTaskExtension>() != null }
                        .configureEach {
                            sourceSets(subproject.the<SourceSetContainer>().named("main").get())
                            executionData(this)
                        }
                }
        }

        reports {
            html.required.set(true)
            xml.required.set(true)
            csv.required.set(false)
        }
    }

    build {
        dependsOn(jacocoReport)
    }
}
