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
                "io.github.airflux.serialization.*",
                "io.github.airflux.json.dsl"
            )
        }

        projects {
            excludes += listOf("airflux-bom", "quickstart")
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
