tasks.register("clean") {
    dependsOn(gradle.includedBuild("airflux-core").task(":clean"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":clean"))
    dependsOn(gradle.includedBuild("airflux-jackson-parser").task(":clean"))
}

tasks.register("build") {
    dependsOn(gradle.includedBuild("airflux-core").task(":build"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":build"))
    dependsOn(gradle.includedBuild("airflux-jackson-parser").task(":build"))
}
