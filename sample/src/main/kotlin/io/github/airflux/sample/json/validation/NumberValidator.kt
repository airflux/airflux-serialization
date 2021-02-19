package io.github.airflux.sample.json.validation

import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.BaseNumberValidators
import io.github.airflux.sample.json.error.JsonErrors

object NumberValidator {

    fun <T> min(value: T): JsValidator<T, JsonErrors.Validation.Numbers>
        where T : Number,
              T : Comparable<T> =
        BaseNumberValidators.min(
            expected = value,
            error = { expected: T, actual: T -> JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual) }
        )

    fun <T> max(value: T): JsValidator<T, JsonErrors.Validation.Numbers>
        where T : Number,
              T : Comparable<T> =
        BaseNumberValidators.max(
            expected = value,
            error = { expected: T, actual: T -> JsonErrors.Validation.Numbers.Max(expected = expected, actual = actual) }
        )

    fun <T> gt(value: T): JsValidator<T, JsonErrors.Validation.Numbers>
        where T : Number,
              T : Comparable<T> =
        BaseNumberValidators.gt(
            expected = value,
            error = { expected: T, actual: T -> JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual) }
        )
}
