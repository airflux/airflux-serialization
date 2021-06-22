package io.github.airflux.sample.json.validation

import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.base.BaseArrayValidators
import io.github.airflux.sample.json.error.JsonErrors

object ArrayValidator {

    fun <T, C : Collection<T>> minItems(value: Int) =
        BaseArrayValidators.minItems<T, C, JsonErrors.Validation.Arrays>(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Arrays.MinItems(expected = expected, actual = actual) }
        )

    fun <T, C : Collection<T>> maxItems(value: Int) =
        BaseArrayValidators.maxItems<T, C, JsonErrors.Validation.Arrays>(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Arrays.MaxItems(expected = expected, actual = actual) }
        )

    fun <T, K> isUnique(keySelector: (T) -> K): JsPropertyValidator<List<T>, JsonErrors.Validation.Arrays> =
        BaseArrayValidators.isUnique(
            keySelector = keySelector,
            error = { index, value: K -> JsonErrors.Validation.Arrays.Unique(index = index, value = value) }
        )
}
