package io.github.airflux.dsl.reader.`object`.builder.validator.std

import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsObjectValidator
import io.github.airflux.std.validator.`object`.MinPropertiesObjectValidator

internal class MinPropertiesObjectValidatorBuilder(private val value: Int) : JsObjectValidatorBuilder.After {
    override fun build(properties: JsObjectProperties): JsObjectValidator.After = MinPropertiesObjectValidator(value)
}
