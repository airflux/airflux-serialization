package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.reader.validator.JsObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.IsNotEmptyObjectValidator
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

internal object IsNotEmptyObjectValidatorBuilder : JsObjectValidatorBuilder.After {
    private val validator = IsNotEmptyObjectValidator()
    override fun build(properties: JsObjectProperties): JsObjectValidator.After = validator
}
