package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

public object ArrayValidator {

    /**
     * If the array contains no unique elements then an error, otherwise a success.
     */
    public fun <T, K : Any> isUnique(keySelector: (T) -> K): JsArrayValidatorBuilder.After<T> =
        IsUniqueArrayValidatorBuilder(keySelector)

    /**
     * If a number of elements in the array are less than an expected [value] then an error, otherwise a success.
     */
    public fun minItems(value: Int): JsArrayValidatorBuilder.Before = MinItemsArrayValidatorBuilder(value)

    /**
     * If a number of elements in the array are more than an expected [value] then an error, otherwise a success.
     */
    public fun maxItems(value: Int): JsArrayValidatorBuilder.Before = MaxItemsArrayValidatorBuilder(value)
}
