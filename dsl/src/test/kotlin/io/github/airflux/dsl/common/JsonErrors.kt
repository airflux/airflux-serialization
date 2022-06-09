package io.github.airflux.dsl.common

import io.github.airflux.core.reader.result.JsError

internal sealed class JsonErrors : JsError {

    sealed class Validation : JsonErrors() {

        sealed class Object : Validation() {
            object AdditionalProperties : Object()
            object IsEmpty : Object()
            data class MinProperties(val expected: Int, val actual: Int) : Object()
            data class MaxProperties(val expected: Int, val actual: Int) : Object()
        }
    }
}
