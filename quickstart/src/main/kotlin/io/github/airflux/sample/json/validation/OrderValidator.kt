package io.github.airflux.sample.json.validation

import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.base.BaseOrderValidators
import io.github.airflux.sample.json.error.JsonErrors

object OrderValidator {

    fun <T : Comparable<T>> min(value: T): JsPropertyValidator<T> =
        BaseOrderValidators.min(
            expected = value,
            error = { expected: T, actual: T ->
                JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
            }
        )

    fun <T : Comparable<T>> max(value: T): JsPropertyValidator<T> =
        BaseOrderValidators.max(
            expected = value,
            error = { expected: T, actual: T ->
                JsonErrors.Validation.Numbers.Max(expected = expected, actual = actual)
            }
        )

    fun <T : Comparable<T>> gt(value: T): JsPropertyValidator<T> =
        BaseOrderValidators.gt(
            expected = value,
            error = { expected: T, actual: T -> JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual) }
        )
}
