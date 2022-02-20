package io.github.airflux.quickstart.json.validation

import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.base.BaseArrayValidators
import io.github.airflux.quickstart.json.error.JsonErrors

object ArrayValidator {

    fun <T, C : Collection<T>> minItems(value: Int) =
        BaseArrayValidators.minItems<T, C>(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Arrays.MinItems(expected = expected, actual = actual) }
        )

    fun <T, C : Collection<T>> maxItems(value: Int) =
        BaseArrayValidators.maxItems<T, C>(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Arrays.MaxItems(expected = expected, actual = actual) }
        )

    fun <T, K> isUnique(failFast: Boolean = true, keySelector: (T) -> K): JsValidator<List<T>> =
        BaseArrayValidators.isUnique(
            failFast = failFast,
            keySelector = keySelector,
            error = { index, value: K -> JsonErrors.Validation.Arrays.Unique(index = index, value = value) }
        )
}
