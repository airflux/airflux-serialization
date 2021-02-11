rootProject.name = "airflux"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

includeBuild("core")
includeBuild("dsl")
includeBuild("jackson-parser")
