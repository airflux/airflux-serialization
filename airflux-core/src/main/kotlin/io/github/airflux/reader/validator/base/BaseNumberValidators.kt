package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

@Suppress("unused")
object BaseNumberValidators {

    fun <T, E> min(expected: T, error: (expected: T, actual: T) -> E): JsValidator<T, E>
        where T : Number,
              T : Comparable<T>,
              E : JsError.Validation =
        JsValidator { value ->
            if (value < expected)
                JsValidationResult.Failure(error(expected, value))
            else
                JsValidationResult.Success
        }

    fun <T, E> max(expected: T, error: (expected: T, actual: T) -> E): JsValidator<T, E>
        where T : Number,
              T : Comparable<T>,
              E : JsError.Validation =
        JsValidator { value ->
            if (value > expected)
                JsValidationResult.Failure(error(expected, value))
            else
                JsValidationResult.Success
        }
}
