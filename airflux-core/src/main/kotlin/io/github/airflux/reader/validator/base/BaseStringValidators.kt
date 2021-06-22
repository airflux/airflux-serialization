package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseStringValidators {

    fun <E> minLength(expected: Int, error: (expected: Int, actual: Int) -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.length < expected) listOf(error(expected, value.length)) else emptyList()
        }

    fun <E> maxLength(expected: Int, error: (expected: Int, actual: Int) -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.length > expected) listOf(error(expected, value.length)) else emptyList()
        }

    fun <E> isNotEmpty(error: () -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.isEmpty()) listOf(error()) else emptyList()
        }

    fun <E> isNotBlank(error: () -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.isBlank()) listOf(error()) else emptyList()
        }

    fun <E> pattern(pattern: Regex, error: (value: String, pattern: Regex) -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (pattern.matches(value)) emptyList() else listOf(error(value, pattern))
        }

    fun <E> isA(predicate: (String) -> Boolean, error: (value: String) -> JsError): JsPropertyValidator<String, JsError>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (predicate(value)) emptyList() else listOf(error(value))
        }
}
