package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

public object ObjectValidator {
    public val additionalProperties: JsObjectValidatorBuilder.Before = AdditionalPropertiesValidatorBuilder
    public val isNotEmpty: JsObjectValidatorBuilder.After = IsNotEmptyValidatorBuilder
    public fun minProperties(value: Int): JsObjectValidatorBuilder.After = MinPropertiesValidatorBuilder(value)
    public fun maxProperties(value: Int): JsObjectValidatorBuilder.After = MaxPropertiesValidatorBuilder(value)
}
