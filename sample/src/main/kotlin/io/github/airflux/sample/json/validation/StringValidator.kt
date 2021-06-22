package io.github.airflux.sample.json.validation

import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.base.BaseStringValidators
import io.github.airflux.sample.json.error.JsonErrors

object StringValidator {

    fun minLength(value: Int): JsPropertyValidator<String, JsonErrors.Validation.Strings> =
        BaseStringValidators.minLength(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Strings.MinLength(expected, actual) }
        )

    fun maxLength(value: Int): JsPropertyValidator<String, JsonErrors.Validation.Strings> =
        BaseStringValidators.maxLength(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Strings.MaxLength(expected, actual) }
        )

    val isNotBlank: JsPropertyValidator<String, JsonErrors.Validation.Strings> =
        BaseStringValidators.isNotBlank { JsonErrors.Validation.Strings.IsEmpty }
}
