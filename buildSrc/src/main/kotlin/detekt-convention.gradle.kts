import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detectConfigPath = "${project.rootProject.projectDir}/config/detekt/detekt.yml"

configure<DetektExtension> {
    ignoreFailures = true
    toolVersion = Configuration.Versions.Detect.Tool
    config = project.files(detectConfigPath)
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
