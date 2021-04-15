package io.github.airflux.sample.json.validation

import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.BaseNumberValidators
import io.github.airflux.sample.json.error.JsonErrors

object NumberValidator {

    fun <T : Comparable<T>> min(value: T): JsValidator<T, JsonErrors.Validation.Numbers> =
        BaseNumberValidators.min(
            expected = value,
            error = { expected: T, actual: T ->
                JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
            }
        )

    fun <T : Comparable<T>> max(value: T): JsValidator<T, JsonErrors.Validation.Numbers> =
        BaseNumberValidators.max(
            expected = value,
            error = { expected: T, actual: T ->
                JsonErrors.Validation.Numbers.Max(expected = expected, actual = actual)
            }
        )

    fun <T : Comparable<T>> gt(value: T): JsValidator<T, JsonErrors.Validation.Numbers> =
        BaseNumberValidators.gt(
            expected = value,
            error = { expected: T, actual: T -> JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual) }
        )
}
