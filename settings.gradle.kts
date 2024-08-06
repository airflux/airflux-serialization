rootProject.name = "airflux"

include(":airflux-serialization-common")
project(":airflux-serialization-common").projectDir = file("./serialization/common")

include(":airflux-serialization-core")
project(":airflux-serialization-core").projectDir = file("./serialization/core")

include(":airflux-serialization-dsl")
project(":airflux-serialization-dsl").projectDir = file("./serialization/dsl")

include(":airflux-serialization-std")
project(":airflux-serialization-std").projectDir = file("./serialization/std")

include(":airflux-serialization-test-core")
project(":airflux-serialization-test-core").projectDir = file("./serialization/test/core")

include(":airflux-serialization-parser-common")
project(":airflux-serialization-parser-common").projectDir = file("./serialization/parser/common")

include(":airflux-parser-jackson")
project(":airflux-parser-jackson").projectDir = file("./parser/jackson")

include(":airflux-serialization-parser-json")
project(":airflux-serialization-parser-json").projectDir = file("./serialization/parser/json")

include(":airflux-bom")
project(":airflux-bom").projectDir = file("./bom")

include(":quickstart")
project(":quickstart").projectDir = file("./quickstart")

dependencyResolutionManagement {
    versionCatalogs {
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
    }
}
