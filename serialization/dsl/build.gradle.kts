plugins {
    id("kotlin-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":serialization-core"))

    /* Kotlin */
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    /* Test */
    testImplementation(testLibs.bundles.kotest)
    testImplementation(project(":serialization-test-core"))
    testImplementation(project(":serialization-kotest-assertions"))
}
