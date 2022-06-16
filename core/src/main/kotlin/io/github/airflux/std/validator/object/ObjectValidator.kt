package io.github.airflux.std.validator.`object`

import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.dsl.reader.`object`.builder.validator.std.AdditionalPropertiesObjectValidatorBuilder
import io.github.airflux.dsl.reader.`object`.builder.validator.std.IsNotEmptyObjectValidatorBuilder
import io.github.airflux.dsl.reader.`object`.builder.validator.std.MaxPropertiesObjectValidatorBuilder
import io.github.airflux.dsl.reader.`object`.builder.validator.std.MinPropertiesObjectValidatorBuilder

public object ObjectValidator {
    public val additionalProperties: JsObjectValidatorBuilder.Before = AdditionalPropertiesObjectValidatorBuilder
    public val isNotEmpty: JsObjectValidatorBuilder.After = IsNotEmptyObjectValidatorBuilder
    public fun minProperties(value: Int): JsObjectValidatorBuilder.After = MinPropertiesObjectValidatorBuilder(value)
    public fun maxProperties(value: Int): JsObjectValidatorBuilder.After = MaxPropertiesObjectValidatorBuilder(value)
}
