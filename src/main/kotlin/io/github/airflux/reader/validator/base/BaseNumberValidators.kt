package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

@Suppress("unused")
object BaseNumberValidators {

    fun <T, E> min(error: (expected: T, actual: T) -> E): (T) -> JsValidator<T, E>
        where T : Number,
              T : Comparable<T>,
              E : JsError.Validation.Reason =
        { expected ->
            JsValidator { value ->
                if (value < expected)
                    JsValidationResult.Failure(error(expected, value))
                else
                    JsValidationResult.Success
            }
        }

    fun <T, E> max(error: (expected: T, actual: T) -> E): (T) -> JsValidator<T, E>
        where T : Number,
              T : Comparable<T>,
              E : JsError.Validation.Reason =
        { expected ->
            JsValidator { value ->
                if (value > expected)
                    JsValidationResult.Failure(error(expected, value))
                else
                    JsValidationResult.Success
            }
        }
}
