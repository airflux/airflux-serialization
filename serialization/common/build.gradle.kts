plugins {
    id("kotlin-internal-library-convention")
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))

    /* Test */
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.pitest.junit5)
}
