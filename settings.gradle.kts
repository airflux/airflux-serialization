rootProject.name = "airflux"

include(":airflux-serialization")
project(":airflux-serialization").projectDir = file("./serialization")

include(":airflux-jackson-parser")
project(":airflux-jackson-parser").projectDir = file("./parser/jackson")

include(":airflux-bom")
project(":airflux-bom").projectDir = file("./bom")

include(":quickstart")
project(":quickstart").projectDir = file("./quickstart")
