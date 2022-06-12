package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.reader.validator.JsObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.IsNotEmptyValidator
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

internal object IsNotEmptyValidatorBuilder : JsObjectValidatorBuilder.After {
    private val validator = IsNotEmptyValidator()
    override fun build(properties: JsObjectProperties): JsObjectValidator.After = validator
}
