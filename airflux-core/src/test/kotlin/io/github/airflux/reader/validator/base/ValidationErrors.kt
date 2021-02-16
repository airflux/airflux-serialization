package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError

sealed class ValidationErrors : JsError.Validation() {

    object Numbers {
        class Min(val expected: Int, val actual: Int) : ValidationErrors()
        class Max(val expected: Int, val actual: Int) : ValidationErrors()
        class Eq(val expected: Int, val actual: Int) : ValidationErrors()
        class Ne(val expected: Int, val actual: Int) : ValidationErrors()
        class Gt(val expected: Int, val actual: Int) : ValidationErrors()
        class Ge(val expected: Int, val actual: Int) : ValidationErrors()
        class Lt(val expected: Int, val actual: Int) : ValidationErrors()
        class Le(val expected: Int, val actual: Int) : ValidationErrors()
    }

    object Arrays {
        class MinItems(val expected: Int, val actual: Int) : ValidationErrors()
        class MaxItems(val expected: Int, val actual: Int) : ValidationErrors()
        class Unique<T>(val index: Int, val value: T) : ValidationErrors()
    }

    object Strings {
        class MinLength(val expected: Int, val actual: Int) : ValidationErrors()
        class MaxLength(val expected: Int, val actual: Int) : ValidationErrors()
        class Pattern(val value: String, val regex: Regex) : ValidationErrors()
        class IsA(val value: String) : ValidationErrors()
        object IsEmpty : ValidationErrors()
        object IsBlank : ValidationErrors()
    }
}
