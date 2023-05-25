plugins {
    id("kotlin-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Kotlin */
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    /* Test */
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.pitest.junit5)
    testImplementation(project(":airflux-serialization-test-core"))
}
