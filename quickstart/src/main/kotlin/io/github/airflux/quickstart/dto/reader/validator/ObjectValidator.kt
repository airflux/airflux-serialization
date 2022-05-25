package io.github.airflux.quickstart.dto.reader.validator

import io.github.airflux.dsl.reader.`object`.validator.base.AdditionalPropertiesValidator
import io.github.airflux.dsl.reader.`object`.validator.base.IsNotEmptyObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.base.MaxPropertiesValidator
import io.github.airflux.dsl.reader.`object`.validator.base.MinPropertiesValidator
import io.github.airflux.quickstart.json.error.JsonErrors

val additionalProperties = AdditionalPropertiesValidator.Builder { unknownProperties ->
    JsonErrors.Validation.Object.AdditionalProperties(unknownProperties)
}

val minProperties = MinPropertiesValidator.Builder { expected: Int, actual: Int ->
    JsonErrors.Validation.Object.MinProperties(expected, actual)
}

val maxProperties = MaxPropertiesValidator.Builder { expected: Int, actual: Int ->
    JsonErrors.Validation.Object.MaxProperties(expected, actual)
}

val isNotEmptyObject = IsNotEmptyObjectValidator {
    JsonErrors.Validation.Object.IsEmpty
}
