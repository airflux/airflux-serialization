package io.github.airflux.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.plugins.signing.SigningExtension

@Suppress("unused")
class AirfluxPublishingPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            plugins.apply("org.gradle.maven-publish")
            plugins.apply("org.gradle.signing")

            extensions.configure(
                SigningExtension::class.java,
                ActionClosure<SigningExtension> {
                    val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
                    val signingKeyPassphrase: String? = System.getenv("GPG_PRIVATE_PASSWORD")
                    isRequired = signingKey != null && signingKey != ""
                    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
                }
            )
        }
    }

    companion object {

        val mavenCentralMetadata: MavenPom.() -> Unit = {
            name.set("Airflux")
            description.set("The library to parse, validate and generate data in the JSON (JavaScript Object Notation) format.")
            url.set("https://airflux.github.io/airflux/")
            licenses(ActionClosure<MavenPomLicenseSpec> {
                license(ActionClosure<MavenPomLicense> {
                    name.set("Mozilla Public License Version 2.0")
                    url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                })
            })

            developers(ActionClosure<MavenPomDeveloperSpec> {
                developer(ActionClosure<MavenPomDeveloper> {
                    id.set("maxim-sambulat")
                    name.set("Maxim Sambulat")
                    email.set("airflux.github.io@gmail.com")
                    organization.set("airflux")
                })
            })

            scm(ActionClosure<MavenPomScm> {
                connection.set("scm:git:git://github.com/airflux/airflux.git")
                developerConnection.set("scm:git:ssh://github.com:airflux/airflux.git")
                url.set("https://github.com/airflux/airflux/tree/main")
            })
        }

        fun RepositoryHandler.mavenSonatypeRepository(project: Project) {
            maven(ActionClosure<MavenArtifactRepository> {
                name = "sonatype"
                url = if (project.version.toString().endsWith("SNAPSHOT"))
                    project.uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else
                    project.uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

                credentials(ActionClosure<PasswordCredentials> {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_PASSWORD")
                })
            })
        }
    }
}
