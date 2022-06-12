package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.validator.JsArrayValidator
import io.github.airflux.core.reader.validator.std.array.IsUniqueArrayValidator
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

internal class IsUniqueArrayValidatorBuilder<T, K : Any>(
    private val keySelector: (T) -> K
) : JsArrayValidatorBuilder.After<T> {

    override fun build(): JsArrayValidator.After<T> = IsUniqueArrayValidator(keySelector)
}
