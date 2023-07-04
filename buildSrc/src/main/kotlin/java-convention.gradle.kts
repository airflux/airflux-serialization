plugins {
    `java-library`
}

tasks {
    java {
        sourceCompatibility = Configuration.JVM.compatibility
        targetCompatibility = Configuration.JVM.compatibility
    }
}
