package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseArrayValidators {

    fun <T, C> minItems(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<C>
        where C : Collection<T> =
        JsPropertyValidator { _, _, values ->
            if (values.size < expected) listOf(error(expected, values.size)) else emptyList()
        }

    fun <T, C> maxItems(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<C>
        where C : Collection<T> =
        JsPropertyValidator { _, _, values ->
            if (values.size > expected) listOf(error(expected, values.size)) else emptyList()
        }

    fun <T, K> isUnique(
        failFast: Boolean,
        keySelector: (T) -> K,
        error: (index: Int, value: K) -> JsError
    ): JsPropertyValidator<Collection<T>> =
        JsPropertyValidator { _, _, values ->
            val errors = mutableListOf<JsError>()
            val unique = mutableSetOf<K>()
            values.forEachIndexed { index, item ->
                val key = keySelector(item)
                if (!unique.add(key)) errors.add(error(index, key))
                if (failFast && errors.isNotEmpty()) return@JsPropertyValidator errors
            }
            errors
        }
}
