package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

public object ObjectValidator {
    public val additionalProperties: JsObjectValidatorBuilder.Before = AdditionalPropertiesObjectValidatorBuilder
    public val isNotEmpty: JsObjectValidatorBuilder.After = IsNotEmptyObjectValidatorBuilder
    public fun minProperties(value: Int): JsObjectValidatorBuilder.After = MinPropertiesObjectValidatorBuilder(value)
    public fun maxProperties(value: Int): JsObjectValidatorBuilder.After = MaxPropertiesObjectValidatorBuilder(value)
}
