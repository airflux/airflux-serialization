package io.github.airflux.dsl.common

import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.value.JsValue

internal sealed class JsonErrors : JsError {

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()
}
