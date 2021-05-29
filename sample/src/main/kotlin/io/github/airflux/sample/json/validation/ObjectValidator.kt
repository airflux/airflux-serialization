package io.github.airflux.sample.json.validation

import io.github.airflux.dsl.reader.`object`.validator.base.additionalPropertiesValidator
import io.github.airflux.dsl.reader.`object`.validator.base.isNotEmptyObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.base.maxPropertiesValidator
import io.github.airflux.dsl.reader.`object`.validator.base.minPropertiesValidator
import io.github.airflux.sample.json.error.JsonErrors

val additionalProperties = additionalPropertiesValidator { unknownProperties ->
    JsonErrors.Validation.Object.AdditionalProperties(unknownProperties)
}

val minProperties = minPropertiesValidator { expected: Int, actual: Int ->
    JsonErrors.Validation.Object.MinProperties(expected, actual)
}

val maxProperties = maxPropertiesValidator { expected: Int, actual: Int ->
    JsonErrors.Validation.Object.MaxProperties(expected, actual)
}

val isNotEmptyObjectValidator = isNotEmptyObjectValidator {
    JsonErrors.Validation.Object.IsEmpty
}
