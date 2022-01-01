import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detectConfigPath = "${project.rootProject.projectDir}/config/detekt/detekt.yml"

configure<DetektExtension> {
    ignoreFailures = true
    config = project.files(detectConfigPath)
    debug = false
    reports{
        html {
            enabled = true
        }
        xml {
            enabled = true
        }
        txt {
            enabled = false
        }
    }
}
