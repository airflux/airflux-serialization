package io.github.airflux.quickstart.dto.reader.validator

import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.base.BaseStringValidators
import io.github.airflux.quickstart.json.error.JsonErrors

object StringValidator {

    fun minLength(value: Int): JsValidator<String> =
        BaseStringValidators.minLength(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Strings.MinLength(expected, actual) }
        )

    fun maxLength(value: Int): JsValidator<String> =
        BaseStringValidators.maxLength(
            expected = value,
            error = { expected, actual -> JsonErrors.Validation.Strings.MaxLength(expected, actual) }
        )

    val isNotBlank: JsValidator<String> =
        BaseStringValidators.isNotBlank { JsonErrors.Validation.Strings.IsEmpty }
}
