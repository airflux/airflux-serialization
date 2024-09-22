// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.exampleLookup01

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct

internal fun main() {
  val source = JsStruct("id" to JsString("123"))
  val key = JsPath.Element.Key("id")
  val result = source.lookup(location = JsLocation.Root, key = key)
  println(result)
}
