import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

dependencies{
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Configuration.Versions.Detekt.Tool}")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:${Configuration.Versions.Detekt.Tool}")
}
configure<DetektExtension> {
    ignoreFailures = true
    toolVersion = Configuration.Versions.Detekt.Tool
    config = project.files("${project.rootProject.projectDir}/config/detekt/detekt.yml")
    baseline = file("${project.rootProject.projectDir}/config/detekt/baseline.xml")
    parallel = true
    debug = false
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = Configuration.JVM.targetVersion
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(true)
    }
}
