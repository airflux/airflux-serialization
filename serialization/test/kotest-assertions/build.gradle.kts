plugins {
    id("kotlin-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))
    implementation(project(":serialization-core"))
    implementation(testLibs.bundles.kotest)
}
