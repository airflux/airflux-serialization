package io.github.airflux.common

import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsValue

sealed class JsonErrors : JsError {

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()
}
