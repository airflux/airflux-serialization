// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.exampleLookupResult02

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct

internal fun main() {
  val source = JsStruct(
     "phones" to JsArray(
         JsString("123")
      )
  )
  val key = JsPath.Element.Key("phones")
  val lookup = source.lookup(location = JsLocation.Root, key = key)
  val result = lookup.apply(0)
  println(result)
}
