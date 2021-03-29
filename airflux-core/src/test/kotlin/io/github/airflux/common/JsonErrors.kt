package io.github.airflux.common

import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsValue
import kotlin.reflect.KClass

sealed class JsonErrors : JsError {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()

    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    sealed class Validation : JsonErrors() {

        sealed class Numbers : Validation() {
            data class Min(val expected: Int, val actual: Int) : Numbers()
            data class Max(val expected: Int, val actual: Int) : Numbers()
            data class Eq(val expected: Int, val actual: Int) : Numbers()
            data class Ne(val expected: Int, val actual: Int) : Numbers()
            data class Gt(val expected: Int, val actual: Int) : Numbers()
            data class Ge(val expected: Int, val actual: Int) : Numbers()
            data class Lt(val expected: Int, val actual: Int) : Numbers()
            data class Le(val expected: Int, val actual: Int) : Numbers()
        }

        sealed class Arrays : Validation() {
            data class MinItems(val expected: Int, val actual: Int) : Arrays()
            data class MaxItems(val expected: Int, val actual: Int) : Arrays()
            data class Unique<T>(val index: Int, val value: T) : Arrays()
        }

        sealed class Strings : Validation() {
            data class MinLength(val expected: Int, val actual: Int) : Strings()
            data class MaxLength(val expected: Int, val actual: Int) : Strings()
            data class Pattern(val value: String, val regex: Regex) : Strings()
            data class IsA(val value: String) : Strings()
            object IsEmpty : Strings()
            object IsBlank : Strings()
        }
    }
}