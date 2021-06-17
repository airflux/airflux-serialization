package io.github.airflux.lookup

import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsValue

sealed class JsLookup {

    abstract val path: JsResultPath

    data class Defined(override val path: JsResultPath, val value: JsValue) : JsLookup()

    sealed class Undefined : JsLookup() {

        data class PathMissing(override val path: JsResultPath) : Undefined()

        data class InvalidType(override val path: JsResultPath, val expected: JsValue.Type, val actual: JsValue.Type) :
            Undefined()
    }
}
