import java.net.URL

buildscript {
    dependencies {
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.5.2")
    }
}


plugins {
    kotlin("jvm") version "1.5.10"
    `java-library`

    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("info.solidsoft.pitest") version "1.5.2"
    id("org.jetbrains.dokka") version "1.4.20"
    jacoco
}

group = "io.github.airflux"
version = "0.0.1-SNAPSHOT"

val jvmTargetVersion by extra { "1.8" }
val junitJupiterVersion by extra { "5.7.0" }
val junitPlatformVersion by extra { "1.7.0" }
val pitestJUnit5Version by extra { "0.12" }

repositories {

    mavenCentral()
    mavenCentral {
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    /* Test */
    testImplementation(kotlin("test-junit5"))

    /* Junit */
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testImplementation("org.junit.platform:junit-platform-engine:$junitPlatformVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")

    /* PITest */
    testImplementation("org.pitest:pitest-junit5-plugin:$pitestJUnit5Version")
}

java {
    withSourcesJar()
}

tasks {

    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = jvmTargetVersion
                suppressWarnings = false
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }

    detekt {
        toolVersion = "1.17.1"
        failFast = false
        this.ignoreFailures = true

        input = files("src/main/kotlin", "src/test/kotlin")
        config = files("${rootProject.projectDir}/config/detekt/detekt.yml")
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

    jacoco {
        toolVersion = "0.8.7"
        reportsDirectory.set(file("$buildDir/reports/jacoco"))
    }

    jacocoTestReport {
        dependsOn(test)
        group = "Reporting"
        description = "Generate Jacoco coverage reports."

        val classFiles = File("${buildDir}/classes/kotlin/main/")
            .walkBottomUp()
            .toSet()
        classDirectories.setFrom(classFiles)
        executionData.setFrom(files("${buildDir}/jacoco/test.exec"))
        reports {
            html.isEnabled = true
            xml.isEnabled = false
            csv.isEnabled = false
        }
    }

    pitest {
        testPlugin.set("junit5")
        junit5PluginVersion.set("0.12")
        pitestVersion.set("1.5.2")
        mutators.set(mutableListOf("STRONGER"))
        outputFormats.set(mutableListOf("XML", "HTML"))
        targetClasses.set(mutableListOf("io.github.airflux.*"))
        targetTests.set(mutableListOf("io.github.airflux.*"))
        avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
    }

    dokkaHtml {
        outputDirectory.set(buildDir.resolve("dokka"))

        dokkaSourceSets {
            named("main") {

                // Used to remove a source set from documentation, test source sets are suppressed by default
                suppress.set(false)

                // Use to include or exclude non public members
                includeNonPublic.set(false)

                // This name will be shown in the final output
                displayName.set("JVM")

                // Platform used for code analysis. See the "Platforms" section of this readme
                platform.set(org.jetbrains.dokka.Platform.jvm)

                samples.from("src/test/kotlin/io/github/airflux/json/samples")
                // Specifies the location of the project source code on the Web.
                // If provided, Dokka generates "source" links for each declaration.
                // Repeat for multiple mappings
                sourceLink {
                    // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                    localDirectory.set(file("src/main/kotlin"))

                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl.set(
                        URL(
                            "https://github.com/airflux/airflux-json/blob/master/src/main/kotlin"
                        )
                    )
                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    remoteLineSuffix.set("#L")
                }

                // Used for linking to JDK documentation
                jdkVersion.set(8)

                // Disable linking to online kotlin-stdlib documentation
                noStdlibLink.set(false)

                // Disable linking to online JDK documentation
                noJdkLink.set(false)
            }
        }
    }
}
