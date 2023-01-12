plugins {
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1-SNAPSHOT"
    group = "io.github.airflux"
}

kover {
    engine.set(kotlinx.kover.api.DefaultIntellijEngine)
}

koverMerged {
    enable()

    filters {
        classes {
            includes += listOf("io.github.airflux.parser.*", "io.github.airflux.serialization.*")
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
