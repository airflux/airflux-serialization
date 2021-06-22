package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseStringValidators {

    fun <E> minLength(expected: Int, error: (expected: Int, actual: Int) -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.length < expected) error(expected, value.length) else null
        }

    fun <E> maxLength(expected: Int, error: (expected: Int, actual: Int) -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.length > expected) error(expected, value.length) else null
        }

    fun <E> isNotEmpty(error: () -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.isEmpty()) error() else null
        }

    fun <E> isNotBlank(error: () -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value.isBlank()) error() else null
        }

    fun <E> pattern(pattern: Regex, error: (value: String, pattern: Regex) -> E): JsPropertyValidator<String, E>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (pattern.matches(value)) null else error(value, pattern)
        }

    fun <E> isA(predicate: (String) -> Boolean, error: (value: String) -> JsError): JsPropertyValidator<String, JsError>
        where E : JsError =
        JsPropertyValidator { _, _, value ->
            if (predicate(value)) null else error(value)
        }
}
