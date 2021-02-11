tasks.register("cleanAll") {
    dependsOn(gradle.includedBuild("core").task(":clean"))
    dependsOn(gradle.includedBuild("dsl").task(":clean"))
    dependsOn(gradle.includedBuild("jackson-parser").task(":clean"))
}

tasks.register("buildAll") {
    dependsOn(gradle.includedBuild("core").task(":build"))
    dependsOn(gradle.includedBuild("dsl").task(":build"))
    dependsOn(gradle.includedBuild("jackson-parser").task(":build"))
}
