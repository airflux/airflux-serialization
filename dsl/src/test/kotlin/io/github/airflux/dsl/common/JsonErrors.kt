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

        sealed class Arrays : Validation() {
            data class MinItems(val expected: Int, val actual: Int) : Arrays()
            data class MaxItems(val expected: Int, val actual: Int) : Arrays()
            data class Unique<T>(val value: T) : Arrays()
        }
    }
}
