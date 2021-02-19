package io.github.airflux.common

import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsValue
import kotlin.reflect.KClass

sealed class JsonErrors : JsError {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()

    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    sealed class Validation : JsonErrors() {

        object Numbers {
            data class Min(val expected: Int, val actual: Int) : Validation()
            data class Max(val expected: Int, val actual: Int) : Validation()
            data class Eq(val expected: Int, val actual: Int) : Validation()
            data class Ne(val expected: Int, val actual: Int) : Validation()
            data class Gt(val expected: Int, val actual: Int) : Validation()
            data class Ge(val expected: Int, val actual: Int) : Validation()
            data class Lt(val expected: Int, val actual: Int) : Validation()
            data class Le(val expected: Int, val actual: Int) : Validation()
        }

        object Arrays {
            data class MinItems(val expected: Int, val actual: Int) : Validation()
            data class MaxItems(val expected: Int, val actual: Int) : Validation()
            data class Unique<T>(val index: Int, val value: T) : Validation()
        }

        object Strings {
            data class MinLength(val expected: Int, val actual: Int) : Validation()
            data class MaxLength(val expected: Int, val actual: Int) : Validation()
            data class Pattern(val value: String, val regex: Regex) : Validation()
            data class IsA(val value: String) : Validation()
            object IsEmpty : Validation()
            object IsBlank : Validation()
        }
    }
}
