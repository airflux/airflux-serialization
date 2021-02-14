package io.github.airflux.sample.json.validation

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.BaseNumberValidators
import io.github.airflux.sample.json.error.ValidationErrors

object NumberValidator {

    fun <T> min(value: T): JsValidator<T, JsError.Validation>
        where T : Number,
              T : Comparable<T> =
        BaseNumberValidators.min(
            expected = value,
            error = { expected: T, actual: T -> ValidationErrors.Numbers.Min(expected = expected, actual = actual) }
        )

    fun <T> max(value: T): JsValidator<T, JsError.Validation>
        where T : Number,
              T : Comparable<T> =
        BaseNumberValidators.max(
            expected = value,
            error = { expected: T, actual: T -> ValidationErrors.Numbers.Max(expected = expected, actual = actual) }
        )
}
