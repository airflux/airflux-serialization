plugins {
    id("kotlin-library-convention")
}

dependencies {
    api(project(":airflux-serialization-parser-common"))
    implementation(project(":serialization-common"))
    implementation(project(":airflux-serialization-core"))

    /* Kotlin */
    implementation(kotlin("stdlib"))

    /* Test */
    testImplementation(project(":airflux-serialization-test-core"))
    testImplementation(testLibs.bundles.kotest)
    testImplementation(project(":serialization-kotest-assertions"))
}
