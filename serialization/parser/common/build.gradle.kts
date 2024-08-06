plugins {
    id("kotlin-library-convention")
}

dependencies {

    /* Test */
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.pitest.junit5)
}
