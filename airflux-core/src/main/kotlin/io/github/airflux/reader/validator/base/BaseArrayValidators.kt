package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseArrayValidators {

    fun <T, C, E> minItems(expected: Int, error: (expected: Int, actual: Int) -> E): JsPropertyValidator<C, E>
        where C : Collection<T>,
              E : JsError =
        JsPropertyValidator { _, _, values ->
            if (values.size < expected) listOf(error(expected, values.size)) else emptyList()
        }

    fun <T, C, E> maxItems(expected: Int, error: (expected: Int, actual: Int) -> E): JsPropertyValidator<C, E>
        where C : Collection<T>,
              E : JsError =
        JsPropertyValidator { _, _, values ->
            if (values.size > expected) listOf(error(expected, values.size)) else emptyList()
        }

    fun <T, K, E> isUnique(
        failFast: Boolean,
        keySelector: (T) -> K,
        error: (index: Int, value: K) -> E
    ): JsPropertyValidator<Collection<T>, E>
        where E : JsError =
        JsPropertyValidator { _, _, values ->
            val errors = mutableListOf<E>()
            val unique = mutableSetOf<K>()
            values.forEachIndexed { index, item ->
                val key = keySelector(item)
                if (!unique.add(key)) errors.add(error(index, key))
                if (failFast && errors.isNotEmpty()) return@JsPropertyValidator errors
            }
            errors
        }
}
