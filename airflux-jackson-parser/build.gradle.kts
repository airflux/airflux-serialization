plugins {
    kotlin("jvm")

    jacoco
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":airflux-core"))

    /* JSON */
    implementation("com.fasterxml.jackson.core:jackson-core:${Versions.Jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.Jackson}") {
        exclude(group = "org.jetbrains.kotlin")
    }

    /* Test */
    testImplementation(kotlin("test-junit5"))

    /* Junit */
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.JUnit.Jupiter}")
    testImplementation("org.junit.platform:junit-platform-engine:${Versions.JUnit.Platform}")
    testImplementation("org.junit.platform:junit-platform-launcher:${Versions.JUnit.Platform}")

    /* PITest */
    testImplementation("org.pitest:pitest-junit5-plugin:${Versions.PiTest.JUnit5}")
}
