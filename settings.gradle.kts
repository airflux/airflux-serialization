rootProject.name = "airflux"

include(":airflux-serialization-core")
project(":airflux-serialization-core").projectDir = file("./serialization/core")

include(":airflux-serialization-dsl")
project(":airflux-serialization-dsl").projectDir = file("./serialization/dsl")

include(":airflux-serialization-std")
project(":airflux-serialization-std").projectDir = file("./serialization/std")

include(":airflux-serialization-test-core")
project(":airflux-serialization-test-core").projectDir = file("./serialization/test/core")

include(":airflux-jackson-parser")
project(":airflux-jackson-parser").projectDir = file("./parser/jackson")

include(":airflux-bom")
project(":airflux-bom").projectDir = file("./bom")

include(":quickstart")
project(":quickstart").projectDir = file("./quickstart")

include(":airflux-parser-api")
project(":airflux-parser-api").projectDir = file("./parser/api")

dependencyResolutionManagement {
    versionCatalogs {
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
    }
}
