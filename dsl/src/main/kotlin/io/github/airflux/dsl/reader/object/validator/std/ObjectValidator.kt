package io.github.airflux.dsl.reader.`object`.validator.std

import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

public object ObjectValidator {
    public val additionalProperties: JsObjectValidatorBuilder.Before = AdditionalProperties
    public val isNotEmpty: JsObjectValidatorBuilder.After = IsNotEmpty
    public fun minProperties(value: Int): JsObjectValidatorBuilder.After = MinProperties(value)
    public fun maxProperties(value: Int): JsObjectValidatorBuilder.After = MaxProperties(value)
}
