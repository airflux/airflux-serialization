package io.github.airflux.sample.json.error

import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsValue
import kotlin.reflect.KClass

sealed class JsonErrors : JsError {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()

    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    sealed class Validation : JsonErrors() {

        sealed class Numbers : Validation() {
            class Min<T>(val expected: T, val actual: T) : Numbers()
            class Max<T>(val expected: T, val actual: T) : Numbers()
            class Gt<T>(val expected: T, val actual: T) : Numbers()
        }

        sealed class Arrays : Validation() {
            class MinItems(val expected: Int, val actual: Int) : Arrays()
            class MaxItems(val expected: Int, val actual: Int) : Arrays()
            class Unique<T>(val index: Int, val value: T) : Arrays()
        }

        sealed class Strings : Validation() {
            class MinLength(val expected: Int, val actual: Int) : Strings()
            class MaxLength(val expected: Int, val actual: Int) : Strings()
            class Pattern(val value: String, val regex: Regex) : Strings()
            class IsA(val value: String) : Strings()
            object IsEmpty : Strings()
            object IsBlank : Strings()
        }
    }
}
