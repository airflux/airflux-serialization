plugins {
    id("kotlin-library-convention")
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
