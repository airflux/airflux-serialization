rootProject.name = "airflux"

include(":serialization-common")
project(":serialization-common").projectDir = file("./serialization/common")

include(":serialization-core")
project(":serialization-core").projectDir = file("./serialization/core")

include(":serialization-dsl")
project(":serialization-dsl").projectDir = file("./serialization/dsl")

include(":serialization-std")
project(":serialization-std").projectDir = file("./serialization/std")

include(":serialization-test-core")
project(":serialization-test-core").projectDir = file("./serialization/test/core")

include(":serialization-kotest-assertions")
project(":serialization-kotest-assertions").projectDir = file("./serialization/test/kotest-assertions")

include(":serialization-parser-common")
project(":serialization-parser-common").projectDir = file("./serialization/parser/common")

include(":parser-jackson")
project(":parser-jackson").projectDir = file("./parser/jackson")

include(":serialization-parser-json")
project(":serialization-parser-json").projectDir = file("./serialization/parser/json")

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
