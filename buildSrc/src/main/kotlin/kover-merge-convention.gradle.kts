plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    reports {
        filters {
            includes {
                classes("io.github.airflux.*")
            }
        }
        total {
            val baseDir = "reports/jacoco/test"
            xml {
                xmlFile.set(layout.buildDirectory.file("$baseDir/jacocoTestReport.xml"))
            }

            html {
                htmlDir.set(layout.buildDirectory.dir("$baseDir/html"))
            }
        }
    }
}
