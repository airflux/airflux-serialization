package io.github.airflux.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class AirfluxConfigurationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.version = Versions.Project
        project.group = Versions.Group
    }
}

@Suppress("unused")
object Versions {
    const val Project = "0.0.1-SNAPSHOT"
    const val Group = "io.github.airflux"

    const val Jackson = "2.12.5"

    object Detect {
        const val Tool = "1.18.1"
    }

    object JaCoCo {
        const val Tool = "0.8.7"
    }

    object JUnit {
        const val Jupiter = "5.8.0"
        const val Platform = "1.8.0"
    }

    object PiTest {
        const val JUnit5 = "0.15"
        const val CLI = "1.7.0"
    }
}
