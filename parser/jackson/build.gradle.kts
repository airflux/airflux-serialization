plugins {
    id("kotlin-library-convention")
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))

    implementation(project(":airflux-serialization-core"))

    /* JSON */
    implementation(libs.bundles.jackson) {
        exclude(group = "org.jetbrains.kotlin")
    }

    /* Test */
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.pitest.junit5)
}
