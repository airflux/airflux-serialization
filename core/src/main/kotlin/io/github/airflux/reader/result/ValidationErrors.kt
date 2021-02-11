package io.github.airflux.reader.result

sealed class ValidationErrors : JsError.Validation.Reason {

    object Numbers {
        class Min(val expected: Int, val actual: Int) : ValidationErrors()
        class Max(val expected: Int, val actual: Int) : ValidationErrors()
    }

    object Arrays {
        class MinItems(val expected: Int, val actual: Int) : ValidationErrors()
        class MaxItems(val expected: Int, val actual: Int) : ValidationErrors()
        class Unique(val index: Int, val value: String) : ValidationErrors()
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
