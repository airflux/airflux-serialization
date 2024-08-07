plugins {
    id("kotlin-library-convention")
}

dependencies {
    /* Kotlin */
    implementation(kotlin("stdlib"))

    api(project(":airflux-serialization-parser-common"))
    implementation(project(":serialization-core"))

    /* JSON */
    api(libs.bundles.jackson) {
        exclude(group = "org.jetbrains.kotlin")
    }

    /* Test */
    testImplementation(testLibs.bundles.kotest)
}
