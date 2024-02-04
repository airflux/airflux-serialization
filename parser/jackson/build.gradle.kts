plugins {
    id("kotlin-library-convention")
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))

    api(project(":airflux-parser-core"))
    implementation(project(":airflux-serialization-core"))

    /* JSON */
    implementation(libs.bundles.jackson) {
        exclude(group = "org.jetbrains.kotlin")
    }

    /* Test */
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.pitest.junit5)
}
