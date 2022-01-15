package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.validator.JsPropertyValidator

@Suppress("unused")
object BaseStringValidators {

    fun minLength(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.length < expected) JsErrors.of(error(expected, value.length)) else null
        }

    fun maxLength(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.length > expected) JsErrors.of(error(expected, value.length)) else null
        }

    fun isNotEmpty(error: () -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.isEmpty()) JsErrors.of(error()) else null
        }

    fun isNotBlank(error: () -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (value.isBlank()) JsErrors.of(error()) else null
        }

    fun pattern(pattern: Regex, error: (value: String, pattern: Regex) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (pattern.matches(value)) null else JsErrors.of(error(value, pattern))
        }

    fun isA(predicate: (String) -> Boolean, error: (value: String) -> JsError): JsPropertyValidator<String> =
        JsPropertyValidator { _, _, value ->
            if (predicate(value)) null else JsErrors.of(error(value))
        }
}
