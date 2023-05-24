import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("info.solidsoft.pitest")
}

tasks {

    configure<PitestPluginExtension> {
        threads.set(4)
        testPlugin.set("junit5")
        junit5PluginVersion.set("0.15")
        pitestVersion.set("1.7.0")
        mutators.set(mutableListOf("STRONGER"))
        outputFormats.set(listOf("XML", "HTML"))
        targetClasses.set(mutableListOf("io.github.airflux.*"))
        targetTests.set(mutableListOf("io.github.airflux.*"))
        avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
        timestampedReports.set(false)
        exportLineCoverage.set(true)
    }
}
