import Configuration.Versions

plugins {
    jacoco
}

configure<JacocoPluginExtension> {
    toolVersion = Versions.JaCoCo.Tool
    reportsDirectory.set(File("${project.buildDir}/reports/jacoco"))
}

tasks.withType<JacocoReport> {
    group = "Reporting"
    description = "Generate Jacoco coverage reports."

    executionData.setFrom(files("${project.buildDir}/jacoco/test.exec"))

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }
}
