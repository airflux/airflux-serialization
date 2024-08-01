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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.6")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.6")
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.15.0")
    implementation("com.github.jk1:gradle-license-report:2.5")
    implementation("org.jetbrains.kotlinx.binary-compatibility-validator:org.jetbrains.kotlinx.binary-compatibility-validator.gradle.plugin:0.16.2")
}
