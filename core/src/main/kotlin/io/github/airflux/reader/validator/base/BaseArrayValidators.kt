package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

@Suppress("unused")
object BaseArrayValidators {

    fun <T, C, E> minItems(error: (expected: Int, actual: Int) -> E): (Int) -> JsValidator<C, E>
        where C : Collection<T>,
              E : JsError.Validation.Reason =
        { expected: Int ->
            JsValidator { values ->
                if (values.size < expected)
                    JsValidationResult.Failure(error(expected, values.size))
                else
                    JsValidationResult.Success
            }
        }

    fun <T, C, E> maxItems(error: (expected: Int, actual: Int) -> E): (Int) -> JsValidator<C, E>
        where C : Collection<T>,
              E : JsError.Validation.Reason =
        { expected: Int ->
            JsValidator { values ->
                if (values.size > expected)
                    JsValidationResult.Failure(error(expected, values.size))
                else
                    JsValidationResult.Success
            }
        }

    fun <T, K, E> isUnique(error: (index: Int, value: K) -> E): ((T) -> K) -> JsValidator<Collection<T>, E>
        where E : JsError.Validation.Reason =
        { keySelector: (T) -> K ->
            JsValidator { values ->
                val unique = mutableSetOf<K>()
                values.forEachIndexed { index, item ->
                    val key = keySelector(item)
                    if (!unique.add(key))
                        return@JsValidator JsValidationResult.Failure(error(index, key))

                }
                JsValidationResult.Success
            }
        }
}
