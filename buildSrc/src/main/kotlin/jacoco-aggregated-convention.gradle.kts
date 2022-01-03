import Configuration.Versions

plugins {
    jacoco
}

configure<JacocoPluginExtension> {
    toolVersion = Versions.JaCoCo.Tool
    reportsDirectory.set(File("${project.buildDir}/reports/jacoco"))
}

val jacocoAggregatedReport = tasks.register<JacocoReport>("jacocoAggregatedReport") {
    group = "Reporting"
    description = "Generates an aggregate report from all subprojects"

    dependsOn(subprojects.map { it.tasks.withType<Test>() })

    val jacocoSubprojects = subprojects.filter { it.tasks.withType<JacocoReport>().isNotEmpty() }
    val sourceSets = jacocoSubprojects.map { subproject -> subproject.the<SourceSetContainer>()["main"] }

    val srcDirs = sourceSets.flatMap { sourceSet -> sourceSet.allSource.srcDirs }
    additionalSourceDirs.from(files(srcDirs))
    sourceDirectories.from(files(srcDirs))

    val output = sourceSets.flatMap { sourceSet -> sourceSet.output }
    classDirectories.from(files(output))

    val executionDataPaths = jacocoSubprojects.map { subproject -> "${subproject.buildDir}/jacoco/test.exec" }
    executionData.from(files(executionDataPaths))

    reports {
        val baseDir = "reports/jacoco/test"

        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("$baseDir/html"))

        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("$baseDir/jacocoTestReport.xml"))

        csv.required.set(true)
        csv.outputLocation.set(layout.buildDirectory.file("$baseDir/jacocoTestReport.csv"))
    }
}
