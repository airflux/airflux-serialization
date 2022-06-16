package io.github.airflux.dsl.reader.array.builder.validator.std

import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsArrayValidator
import io.github.airflux.std.validator.array.IsNotEmptyArrayValidator

internal object IsNotEmptyArrayValidatorBuilder : JsArrayValidatorBuilder.Before {
    private val validator = IsNotEmptyArrayValidator()
    override fun build(): JsArrayValidator.Before = validator
}
