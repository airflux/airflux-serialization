rootProject.name = "airflux"

include(":airflux-core")
project(":airflux-core").projectDir = file("./core")

include(":airflux-dsl")
project(":airflux-dsl").projectDir = file("./dsl")

include(":airflux-jackson-parser")
project(":airflux-jackson-parser").projectDir = file("./parser/jackson")

include(":airflux-bom")
project(":airflux-bom").projectDir = file("./bom")

include(":quickstart")
project(":quickstart").projectDir = file("./quickstart")
