package io.github.airflux.dsl.reader.array.builder.validator.std

import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsArrayValidator
import io.github.airflux.std.validator.array.IsUniqueArrayValidator

internal class IsUniqueArrayValidatorBuilder<T, K : Any>(
    private val keySelector: (T) -> K
) : JsArrayValidatorBuilder.After<T> {

    override fun build(): JsArrayValidator.After<T> = IsUniqueArrayValidator(keySelector)
}
