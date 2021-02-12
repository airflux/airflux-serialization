tasks.register("clean") {
    dependsOn(gradle.includedBuild("airflux-core").task(":clean"))
    dependsOn(gradle.includedBuild("dsl").task(":clean"))
    dependsOn(gradle.includedBuild("jackson-parser").task(":clean"))
}

tasks.register("build") {
    dependsOn(gradle.includedBuild("airflux-core").task(":build"))
    dependsOn(gradle.includedBuild("dsl").task(":build"))
    dependsOn(gradle.includedBuild("jackson-parser").task(":build"))
}
