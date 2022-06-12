package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.validator.JsArrayValidator
import io.github.airflux.core.reader.validator.std.array.IsNotEmptyArrayValidator
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

internal object IsNotEmptyArrayValidatorBuilder : JsArrayValidatorBuilder.Before {
    private val validator = IsNotEmptyArrayValidator()
    override fun build(): JsArrayValidator.Before = validator
}
