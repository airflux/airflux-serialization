package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseStringValidators {

    fun minLength(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.length < expected) listOf(error(expected, value.length)) else emptyList()
        }

    fun maxLength(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.length > expected) listOf(error(expected, value.length)) else emptyList()
        }

    fun isNotEmpty(error: () -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.isEmpty()) listOf(error()) else emptyList()
        }

    fun isNotBlank(error: () -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.isBlank()) listOf(error()) else emptyList()
        }

    fun pattern(pattern: Regex, error: (value: String, pattern: Regex) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (pattern.matches(value)) emptyList() else listOf(error(value, pattern))
        }

    fun isA(predicate: (String) -> Boolean, error: (value: String) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (predicate(value)) emptyList() else listOf(error(value))
        }
}
