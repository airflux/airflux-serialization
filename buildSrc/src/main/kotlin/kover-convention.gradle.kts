import kotlinx.kover.api.KoverTaskExtension

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
}

tasks {

    withType<Test> {
        extensions.configure(KoverTaskExtension::class) {
            isDisabled = false
        }
    }

    val baseDir = "reports/jacoco/test"
    val isCI = System.getenv().containsKey("CI")
    koverHtmlReport {
        isEnabled = !isCI
        htmlReportDir.set(layout.buildDirectory.dir("$baseDir/html"))
    }
    koverXmlReport {
        isEnabled = true
        xmlReportFile.set(layout.buildDirectory.file("$baseDir/jacocoTestReport.xml"))
    }
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)
    jacocoEngineVersion.set(Configuration.Versions.JaCoCo.Tool)
    disabledProjects = setOf("quickstart")
}
