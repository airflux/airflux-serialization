plugins {
    kotlin("jvm")
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    /* Test */
    testImplementation(kotlin("test-junit5"))

    /* Junit */
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.platform:junit-platform-engine:${Versions.JUnit.Platform}")
    testImplementation("org.junit.platform:junit-platform-launcher:${Versions.JUnit.Platform}")

    /* PITest */
    testImplementation("org.pitest:pitest-junit5-plugin:${Versions.PiTest.JUnit5}")
}

pitest {
    threads.set(4)
    testPlugin.set("junit5")
    junit5PluginVersion.set(Versions.PiTest.JUnit5)
    pitestVersion.set(Versions.PiTest.CLI)
    mutators.set(mutableListOf("STRONGER"))
    outputFormats.set(listOf("XML", "HTML"))
    targetClasses.set(mutableListOf("io.github.airflux.*"))
    targetTests.set(mutableListOf("io.github.airflux.*"))
    avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
    timestampedReports.set(false)
    exportLineCoverage.set(true)
}

tasks {
    build {
        dependsOn(pitest)
    }
}
