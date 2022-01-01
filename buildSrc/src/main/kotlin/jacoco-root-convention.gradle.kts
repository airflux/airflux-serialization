import Configuration.Versions

plugins {
    jacoco
}

configure<JacocoPluginExtension> {
    toolVersion = Versions.JaCoCo.Tool
    reportsDirectory.set(File("${project.buildDir}/reports/jacoco"))
}

val jacocoRootReport = tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Reporting"
    description = "Generates an aggregate report from all subprojects"

    dependsOn(subprojects.map { it.tasks.withType<Test>() })
//    dependsOn(subprojects.map { it.tasks.withType<JacocoReport>() } )

    val jacocoSubprojects = subprojects.filter { it.tasks.withType<JacocoReport>().isNotEmpty() }

//    val jacocoSubprojects = subprojects.filter { subproject -> subproject.tasks.findByName("jacocoTestReport") != null }
    val sourceSets = jacocoSubprojects.map { subproject -> subproject.the<SourceSetContainer>()["main"] }

    val srcDirs = sourceSets.flatMap { sourceSet -> sourceSet.allSource.srcDirs }
    additionalSourceDirs.from(files(srcDirs))
    sourceDirectories.from(files(srcDirs))

    val output = sourceSets.flatMap { sourceSet -> sourceSet.output }
    classDirectories.from(files(output))

    val executionDataPaths = jacocoSubprojects.map { subproject -> "${subproject.buildDir}/jacoco/test.exec" }
    executionData.from(files(executionDataPaths))

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }
}
