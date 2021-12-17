buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.7.0")
    }
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("org.jacoco:org.jacoco.core:0.8.7")
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.7.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.18.1")
}

gradlePlugin {

    with(plugins) {
        register("airflux-configuration-plugin") {
            id = "airflux-configuration-plugin"
            implementationClass = "io.github.airflux.gradle.AirfluxConfigurationPlugin"
        }

        register("airflux-jacoco-plugin") {
            id = "airflux-jacoco-plugin"
            implementationClass = "io.github.airflux.gradle.AirfluxJacocoPlugin"
        }

        register("airflux-pitest-plugin") {
            id = "airflux-pitest-plugin"
            implementationClass = "io.github.airflux.gradle.AirfluxPitestPlugin"
        }

        register("airflux-detekt-plugin") {
            id = "airflux-detekt-plugin"
            implementationClass = "io.github.airflux.gradle.AirfluxDetektPlugin"
        }

        register("airflux-publishing-plugin") {
            id = "airflux-publishing-plugin"
            implementationClass = "io.github.airflux.gradle.AirfluxPublishingPlugin"
        }
    }
}
