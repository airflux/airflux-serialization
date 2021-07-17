plugins {
    kotlin("jvm")

    jacoco
}

val junitJupiterVersion by extra { "5.7.0" }
val junitPlatformVersion by extra { "1.7.0" }
val pitestJUnit5Version by extra { "0.12" }

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

tasks {

    pitest {
        threads.set(4)
        testPlugin.set("junit5")
        junit5PluginVersion.set("0.12")
        pitestVersion.set("1.6.7")
        mutators.set(mutableListOf("STRONGER"))
        outputFormats.set(listOf("XML", "HTML"))
        targetClasses.set(mutableListOf("io.github.airflux.*"))
        targetTests.set(mutableListOf("io.github.airflux.*"))
        avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
        mainSourceSets.set(listOf(project.sourceSets.main.get()))
        timestampedReports.set(false)
    }

    build {
        dependsOn(pitest)
    }
}
