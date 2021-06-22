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
        keySelector: (T) -> K,
        error: (index: Int, value: K) -> E
    ): JsPropertyValidator<Collection<T>, E>
        where E : JsError =
        JsPropertyValidator { _, _, values ->
            val unique = mutableSetOf<K>()
            values.forEachIndexed { index, item ->
                val key = keySelector(item)
                if (!unique.add(key)) return@JsPropertyValidator listOf(error(index, key))
            }
            emptyList()
        }
}
