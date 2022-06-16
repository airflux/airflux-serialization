package io.github.airflux.dsl.reader.`object`.builder.validator.std

import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsObjectValidator
import io.github.airflux.std.validator.`object`.IsNotEmptyObjectValidator

internal object IsNotEmptyObjectValidatorBuilder : JsObjectValidatorBuilder.After {
    private val validator = IsNotEmptyObjectValidator()
    override fun build(properties: JsObjectProperties): JsObjectValidator.After = validator
}
