package io.github.airflux.core.reader.validator.std.array

import io.github.airflux.core.reader.validator.JsValidator

public object ArrayValidator {

    /**
     * If the array contains no unique elements then an error, otherwise a success.
     */
    public fun <T, K : Any> isUnique(keySelector: (T) -> K): JsValidator<Collection<T>> =
        IsUniqueArrayValidator(keySelector)

    /**
     * If a number of elements in the array are less than an expected [value] then an error, otherwise a success.
     */
    public fun <T> minItems(value: Int): JsValidator<Collection<T>> = MinItemsArrayValidator(value)

    /**
     * If a number of elements in the array are more than an expected [value] then an error, otherwise a success.
     */
    public fun <T> maxItems(value: Int): JsValidator<Collection<T>> = MaxItemsArrayValidator(value)
}
