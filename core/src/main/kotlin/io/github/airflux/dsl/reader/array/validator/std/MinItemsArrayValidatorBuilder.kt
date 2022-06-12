package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.validator.JsArrayValidator
import io.github.airflux.core.reader.validator.std.array.MinItemsArrayValidator
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

internal class MinItemsArrayValidatorBuilder(private val value: Int) : JsArrayValidatorBuilder.Before {
    override fun build(): JsArrayValidator.Before = MinItemsArrayValidator(value)
}
