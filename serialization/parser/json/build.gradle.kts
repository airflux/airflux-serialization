plugins {
    id("kotlin-library-convention")
}

dependencies {
    api(project(":serialization-parser-common"))
    implementation(project(":serialization-common"))
    implementation(project(":serialization-core"))

    /* Kotlin */
    implementation(kotlin("stdlib"))

    /* Test */
    testImplementation(project(":serialization-test-core"))
    testImplementation(testLibs.bundles.kotest)
    testImplementation(project(":serialization-kotest-assertions"))
}
