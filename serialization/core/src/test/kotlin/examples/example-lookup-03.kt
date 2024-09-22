// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.exampleLookup03

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct

internal fun main() {
    val source = JsStruct(
        "user" to JsStruct(
            "phones" to JsArray(
                JsString("123")
            )
        )
    )
    val path = JsPath("user").append("phones").append(0)
    val result = source.lookup(location = JsLocation.Root, path = path)
    println(result)
}
