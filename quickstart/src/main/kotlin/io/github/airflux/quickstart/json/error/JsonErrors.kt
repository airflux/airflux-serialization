package io.github.airflux.quickstart.json.error

import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.core.value.JsValue
import kotlin.reflect.KClass

sealed class JsonErrors : JsError {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()

    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    data class EnumCast(val expected: String, val actual: String) : JsonErrors()

    object AdditionalItems : Validation.Arrays()

    sealed class Validation : JsonErrors() {

        sealed class Numbers : Validation() {
            class Min<T>(val expected: T, val actual: T) : Numbers()
            class Max<T>(val expected: T, val actual: T) : Numbers()
            class Eq<T>(val expected: T, val actual: T) : Numbers()
            class Ne<T>(val expected: T, val actual: T) : Numbers()
            class Gt<T>(val expected: T, val actual: T) : Numbers()
            class Ge<T>(val expected: T, val actual: T) : Numbers()
            class Lt<T>(val expected: T, val actual: T) : Numbers()
            class Le<T>(val expected: T, val actual: T) : Numbers()
        }

        sealed class Arrays : Validation() {
            object IsEmpty : Arrays()
            class MinItems(val expected: Int, val actual: Int) : Arrays()
            class MaxItems(val expected: Int, val actual: Int) : Arrays()
            class Unique<T>(val value: T) : Arrays()
        }

        sealed class Strings : Validation() {
            class MinLength(val expected: Int, val actual: Int) : Strings()
            class MaxLength(val expected: Int, val actual: Int) : Strings()
            class Pattern(val value: String, val regex: Regex) : Strings()
            class IsA(val value: String) : Strings()
            object IsEmpty : Strings()
            object IsBlank : Strings()
        }

        sealed class Object : Validation() {
            object IsEmpty : Object()
            object AdditionalProperties : Object()
            class MinProperties(val expected: Int, val actual: Int) : Object()
            class MaxProperties(val expected: Int, val actual: Int) : Object()
        }
    }
}
