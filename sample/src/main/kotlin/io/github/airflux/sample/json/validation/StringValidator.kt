package io.github.airflux.sample.json.validation

import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.BaseStringValidators
import io.github.airflux.sample.json.error.ValidationErrors

object StringValidator {

    fun minLength(value: Int): JsValidator<String, ValidationErrors.Strings> =
        BaseStringValidators.minLength(
            expected = value,
            error = { expected, actual -> ValidationErrors.Strings.MinLength(expected, actual) }
        )

    fun maxLength(value: Int): JsValidator<String, ValidationErrors.Strings> =
        BaseStringValidators.maxLength(
            expected = value,
            error = { expected, actual -> ValidationErrors.Strings.MaxLength(expected, actual) }
        )

    fun isNotBlank(): JsValidator<String, ValidationErrors.Strings.IsEmpty> =
        BaseStringValidators.isNotBlank { ValidationErrors.Strings.IsEmpty }
}
