package io.github.airflux.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer
import java.io.File

@Suppress("unused")
class AirfluxJacocoPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        with(project) {
            plugins.apply(JacocoPlugin::class.java)

            extensions.configure<JacocoPluginExtension> {
                toolVersion = Versions.JaCoCo.Tool
                reportsDirectory.set(File("${project.buildDir}/reports/jacoco"))
            }

            tasks.withType<JacocoReport> {
                group = "Reporting"
                description = "Generate Jacoco coverage reports."
                executionData.setFrom(files("${buildDir}/jacoco/test.exec"))

                reports(ActionClosure<JacocoReportsContainer> {
                    html.required.set(true)
                    xml.required.set(false)
                    csv.required.set(false)
                })
            }
        }
    }
}

