// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.exampleLookup02

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString

internal fun main() {
  val source = JsArray(JsString("1"), JsString("2"), JsString("3"))
  val idx = JsPath.Element.Idx(1)
  val result = source.lookup(location = JsLocation.Root, idx = idx)
  println(result)
}
