package io.github.airflux.sample.json.validation

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.BaseArrayValidators
import io.github.airflux.sample.json.error.ValidationErrors

object ArrayValidator {

    fun <T, C : Collection<T>> minItems(value: Int) =
        BaseArrayValidators.minItems<T, C, JsError.Validation>(
            expected = value,
            error = { expected, actual -> ValidationErrors.Arrays.MinItems(expected = expected, actual = actual) }
        )

    fun <T, C : Collection<T>> maxItems(value: Int) =
        BaseArrayValidators.maxItems<T, C, JsError.Validation>(
            expected = value,
            error = { expected, actual -> ValidationErrors.Arrays.MaxItems(expected = expected, actual = actual) }
        )

    fun <T, K> isUnique(keySelector: (T) -> K): JsValidator<List<T>, ValidationErrors.Arrays.Unique<K>> =
        BaseArrayValidators.isUnique(
            keySelector = keySelector,
            error = { index, value: K -> ValidationErrors.Arrays.Unique(index = index, value = value) }
        )
}
