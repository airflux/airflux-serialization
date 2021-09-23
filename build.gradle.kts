
tasks.register("clean") {
    dependsOn(gradle.includedBuild("plugin-build").task(":clean"))
    dependsOn(gradle.includedBuild("airflux-core").task(":clean"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":clean"))
    dependsOn(gradle.includedBuild("airflux-jackson-parser").task(":clean"))
    dependsOn(gradle.includedBuild("quickstart").task(":clean"))
}

tasks.register("build") {
    dependsOn(gradle.includedBuild("plugin-build").task(":build"))
    dependsOn(gradle.includedBuild("airflux-core").task(":build"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":build"))
    dependsOn(gradle.includedBuild("airflux-jackson-parser").task(":build"))
    dependsOn(gradle.includedBuild("quickstart").task(":build"))
}

tasks.register("jacocoTestReport") {
    dependsOn(gradle.includedBuild("airflux-core").task(":jacocoTestReport"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":jacocoTestReport"))
}

tasks.register("pitest") {
    dependsOn(gradle.includedBuild("airflux-core").task(":pitest"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":pitest"))
}

tasks.register("publishToMavenLocal") {
    dependsOn(gradle.includedBuild("airflux-core").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("airflux-jackson-parser").task(":publishToMavenLocal"))
}

tasks.register("publish") {
    dependsOn(gradle.includedBuild("airflux-core").task(":publish"))
    dependsOn(gradle.includedBuild("airflux-dsl").task(":publish"))
    dependsOn(gradle.includedBuild("airflux-jackson-parser").task(":publish"))
}
