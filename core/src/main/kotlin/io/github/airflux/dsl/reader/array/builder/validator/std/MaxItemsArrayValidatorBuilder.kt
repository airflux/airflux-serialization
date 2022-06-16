package io.github.airflux.dsl.reader.array.builder.validator.std

import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsArrayValidator
import io.github.airflux.std.validator.array.MaxItemsArrayValidator

internal class MaxItemsArrayValidatorBuilder(private val value: Int) : JsArrayValidatorBuilder.Before {
    override fun build(): JsArrayValidator.Before = MaxItemsArrayValidator(value)
}
