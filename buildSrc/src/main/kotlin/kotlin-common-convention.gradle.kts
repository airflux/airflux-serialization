import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")

    id("detekt-convention")
    id("org.jetbrains.kotlinx.kover")
    id("pitest-convention")
}

tasks {

    withType<KotlinCompile>()
        .configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(Configuration.JVM.targetVersion))
                suppressWarnings.set(false)
                freeCompilerArgs.set(
                    listOf(
                        "-Xjsr305=strict",
                        "-Xjvm-default=all",
                        "-Xexplicit-api=strict"
                    )
                )
            }
        }

    withType<Test> {
        useJUnitPlatform()
        reports {
            junitXml.required.set(false)
        }
        systemProperty("gradle.build.dir", project.rootProject.layout.buildDirectory.asFile.get())
    }
}

configure<DetektExtension> {
    source.setFrom(project.files("src/main/kotlin", "src/test/kotlin"))
}

configure<PitestPluginExtension> {
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
}
