buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.15.0")
    }
}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.detekt)
    implementation(libs.gradle.plugin.kover)
    implementation(libs.gradle.plugin.pitest)
    implementation(libs.gradle.plugin.license.report)
    implementation(libs.gradle.plugin.binary.compatibility.validator)
}
