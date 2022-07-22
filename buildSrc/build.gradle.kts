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
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("org.jacoco:org.jacoco.core:0.8.8")
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.7.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.21.0")
    implementation("org.jetbrains.kotlinx:kover:0.5.1")
}
