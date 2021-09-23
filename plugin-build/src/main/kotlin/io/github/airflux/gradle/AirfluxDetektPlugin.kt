package io.github.airflux.gradle

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
class AirfluxDetektPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        with(project) {
            plugins.apply(DetektPlugin::class.java)

            val detectConfigPath = "$projectDir/config/detekt/detekt.yml"

            extensions.configure<DetektExtension> {
                toolVersion = Versions.Detect.Tool
                ignoreFailures = true
                config = project.files(detectConfigPath)
                debug = false
                reports(ActionClosure {
                    html {
                        enabled = true
                    }

                    xml {
                        enabled = true
                    }
                    txt {
                        enabled = false
                    }
                })
            }
        }
    }
}
