package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.core.reader.validator.JsObjectValidator
import io.github.airflux.core.reader.validator.std.`object`.MaxPropertiesObjectValidator
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

internal class MaxPropertiesObjectValidatorBuilder(private val value: Int) : JsObjectValidatorBuilder.After {
    override fun build(properties: JsObjectProperties): JsObjectValidator.After = MaxPropertiesObjectValidator(value)
}
