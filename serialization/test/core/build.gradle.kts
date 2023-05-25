plugins {
    id("kotlin-internal-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))
    implementation(project(":airflux-serialization-core"))
    implementation(testLibs.bundles.kotest)
}
