package io.github.airflux.quickstart.dto.reader.validator

import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.base.BaseOrderValidators
import io.github.airflux.quickstart.json.error.JsonErrors

object OrderValidator {

    fun <T : Comparable<T>> min(value: T): JsValidator<T> =
        BaseOrderValidators.min(
            expected = value,
            error = { expected: T, actual: T ->
                JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
            }
        )

    fun <T : Comparable<T>> max(value: T): JsValidator<T> =
        BaseOrderValidators.max(
            expected = value,
            error = { expected: T, actual: T ->
                JsonErrors.Validation.Numbers.Max(expected = expected, actual = actual)
            }
        )

    fun <T : Comparable<T>> gt(value: T): JsValidator<T> =
        BaseOrderValidators.gt(
            expected = value,
            error = { expected: T, actual: T -> JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual) }
        )
}
