import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPom

@Suppress("unused")
object Configuration {

    object JVM {
        const val targetVersion = "1.8"
    }

    object Publishing {

        val mavenCentralMetadata: MavenPom.() -> Unit = {
            name.set("Airflux")
            description.set("The library to parse, validate and generate data in the JSON (JavaScript Object Notation) format.")
            url.set("https://airflux.github.io/airflux/")
            licenses {
                license {
                    name.set("Mozilla Public License Version 2.0")
                    url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                }
            }

            developers {
                developer {
                    id.set("maxim-sambulat")
                    name.set("Maxim Sambulat")
                    email.set("airflux.github.io@gmail.com")
                    organization.set("airflux")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/airflux/airflux.git")
                developerConnection.set("scm:git:ssh://github.com:airflux/airflux.git")
                url.set("https://github.com/airflux/airflux/tree/main")
            }
        }

        fun RepositoryHandler.mavenSonatypeRepository(project: Project) {
            maven {
                name = "sonatype"
                url = if (project.version.toString().endsWith("SNAPSHOT"))
                    project.uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else
                    project.uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }

    object Versions {

        const val Jackson = "2.12.5"

        object Detect {
            const val Tool = "1.18.1"
        }

        object JaCoCo {
            const val Tool = "0.8.7"
        }

        object Test {

            object JUnit {
                const val Jupiter = "5.8.2"
                const val Platform = "1.8.2"
            }

            object PiTest {
                const val JUnit5 = "0.15"
                const val CLI = "1.7.3"
            }
        }
    }
}
