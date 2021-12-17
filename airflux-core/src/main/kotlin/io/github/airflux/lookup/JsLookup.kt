package io.github.airflux.lookup

import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsValue

sealed class JsLookup {

    abstract val location: JsLocation

    data class Defined(override val location: JsLocation, val value: JsValue) : JsLookup()

    sealed class Undefined : JsLookup() {

        data class PathMissing(override val location: JsLocation) : Undefined()

        data class InvalidType(override val location: JsLocation, val expected: JsValue.Type, val actual: JsValue.Type) :
            Undefined()
    }
}
