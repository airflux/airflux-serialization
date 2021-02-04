package io.github.airflux.lookup

import io.github.airflux.path.JsPath
import io.github.airflux.value.JsValue

sealed class JsLookup {

    abstract val path: JsPath

    data class Defined(override val path: JsPath, val value: JsValue) : JsLookup()

    sealed class Undefined : JsLookup() {

        data class PathMissing(override val path: JsPath) : Undefined()

        data class InvalidType(override val path: JsPath, val expected: JsValue.Type, val actual: JsValue.Type) : Undefined()
    }
}



