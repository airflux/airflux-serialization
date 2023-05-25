import kotlinx.kover.api.DefaultIntellijEngine

plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    engine.set(DefaultIntellijEngine)
}

koverMerged {
    enable()

    filters {
        classes {
            includes += listOf(
                "io.github.airflux.parser.*",
                "io.github.airflux.serialization.core.*",
                "io.github.airflux.serialization.dsl.*",
                "io.github.airflux.serialization.std.*"
            )
        }

        projects {
            excludes += listOf(
                "airflux-bom",
                "airflux-serialization-test-core",
                "quickstart"
            )
        }
    }

    val baseDir = "reports/jacoco/test"
    xmlReport {
        reportFile.set(layout.buildDirectory.file("$baseDir/jacocoTestReport.xml"))
    }

    htmlReport {
        reportDir.set(layout.buildDirectory.dir("$baseDir/html"))
    }
}
