package io.github.airflux.std.validator.array

import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.array.builder.validator.std.IsNotEmptyArrayValidatorBuilder
import io.github.airflux.dsl.reader.array.builder.validator.std.IsUniqueArrayValidatorBuilder
import io.github.airflux.dsl.reader.array.builder.validator.std.MaxItemsArrayValidatorBuilder
import io.github.airflux.dsl.reader.array.builder.validator.std.MinItemsArrayValidatorBuilder

public object ArrayValidator {

    /**
     * If the array contains no unique elements then an error, otherwise a success.
     */
    public fun <T, K : Any> isUnique(keySelector: (T) -> K): JsArrayValidatorBuilder.After<T> =
        IsUniqueArrayValidatorBuilder(keySelector)

    /**
     * If the is empty then an error, otherwise a success.
     */
    public val isNotEmpty: JsArrayValidatorBuilder.Before = IsNotEmptyArrayValidatorBuilder

    /**
     * If a number of elements in the array are less than an expected [value] then an error, otherwise a success.
     */
    public fun minItems(value: Int): JsArrayValidatorBuilder.Before = MinItemsArrayValidatorBuilder(value)

    /**
     * If a number of elements in the array are more than an expected [value] then an error, otherwise a success.
     */
    public fun maxItems(value: Int): JsArrayValidatorBuilder.Before = MaxItemsArrayValidatorBuilder(value)
}
