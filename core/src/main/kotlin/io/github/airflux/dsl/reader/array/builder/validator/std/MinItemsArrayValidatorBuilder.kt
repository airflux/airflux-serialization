package io.github.airflux.dsl.reader.array.builder.validator.std

import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsArrayValidator
import io.github.airflux.std.validator.array.MinItemsArrayValidator

internal class MinItemsArrayValidatorBuilder(private val value: Int) : JsArrayValidatorBuilder.Before {
    override fun build(): JsArrayValidator.Before = MinItemsArrayValidator(value)
}
