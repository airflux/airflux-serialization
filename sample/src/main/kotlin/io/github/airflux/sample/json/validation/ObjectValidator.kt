package io.github.airflux.sample.json.validation

import io.github.airflux.dsl.reader.`object`.validator.base.AdditionalProperties
import io.github.airflux.dsl.reader.`object`.validator.base.IsNotEmptyObject
import io.github.airflux.dsl.reader.`object`.validator.base.MaxProperties
import io.github.airflux.dsl.reader.`object`.validator.base.MinProperties
import io.github.airflux.sample.json.error.JsonErrors

val additionalProperties = AdditionalProperties { unknownProperties ->
    JsonErrors.Validation.Object.AdditionalProperties(unknownProperties)
}

val minProperties = MinProperties { expected: Int, actual: Int ->
    JsonErrors.Validation.Object.MinProperties(expected, actual)
}

val maxProperties = MaxProperties { expected: Int, actual: Int ->
    JsonErrors.Validation.Object.MaxProperties(expected, actual)
}

val isNotEmptyObject = IsNotEmptyObject {
    JsonErrors.Validation.Object.IsEmpty
}
