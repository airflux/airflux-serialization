package io.github.airflux.gradle

import info.solidsoft.gradle.pitest.PitestPlugin
import info.solidsoft.gradle.pitest.PitestPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
class AirfluxPitestPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        with(project) {
            plugins.apply(PitestPlugin::class.java)

            extensions.configure<PitestPluginExtension> {
                threads.set(4)
                testPlugin.set("junit5")
                junit5PluginVersion.set(Versions.PiTest.JUnit5)
                pitestVersion.set(Versions.PiTest.CLI)
                mutators.set(mutableListOf("STRONGER"))
                outputFormats.set(listOf("XML", "HTML"))
                targetClasses.set(mutableListOf("io.github.airflux.*"))
                targetTests.set(mutableListOf("io.github.airflux.*"))
                avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
                timestampedReports.set(false)
                exportLineCoverage.set(true)
            }
        }
    }
}
