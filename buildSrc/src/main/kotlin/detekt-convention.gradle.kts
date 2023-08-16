import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detektVersion = "1.23.0"

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${detektVersion}")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:${detektVersion}")
}

configure<DetektExtension> {
    ignoreFailures = false
    toolVersion = detektVersion
    config.setFrom(project.files("${project.rootProject.projectDir}/config/detekt/detekt.yml"))
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
