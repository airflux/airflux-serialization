import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("info.solidsoft.pitest")
}

tasks {

    configure<PitestPluginExtension> {
        threads.set(4)
        junit5PluginVersion.set("1.0.0")
        pitestVersion.set("1.15.0")
        mutators.set(mutableListOf("STRONGER"))
        outputFormats.set(listOf("XML", "HTML"))
        targetClasses.set(mutableListOf("io.github.airflux.*"))
        targetTests.set(mutableListOf("io.github.airflux.*"))
        avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
        timestampedReports.set(false)
        exportLineCoverage.set(true)
        useClasspathFile.set(true)
    }
}
