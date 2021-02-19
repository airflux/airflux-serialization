package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

@Suppress("unused")
object BaseStringValidators {

    fun <E> minLength(expected: Int, error: (expected: Int, actual: Int) -> E): JsValidator<String, E>
        where E : JsError =
        JsValidator { value ->
            if (value.length < expected)
                JsValidationResult.Failure(error(expected, value.length))
            else
                JsValidationResult.Success
        }

    fun <E> maxLength(expected: Int, error: (expected: Int, actual: Int) -> E): JsValidator<String, E>
        where E : JsError =
        JsValidator { value ->
            if (value.length > expected)
                JsValidationResult.Failure(error(expected, value.length))
            else
                JsValidationResult.Success
        }

    fun <E> isNotEmpty(error: () -> E): JsValidator<String, E>
        where E : JsError =
        JsValidator { value ->
            if (value.isEmpty())
                JsValidationResult.Failure(error())
            else
                JsValidationResult.Success
        }

    fun <E> isNotBlank(error: () -> E): JsValidator<String, E>
        where E : JsError =
        JsValidator { value ->
            if (value.isBlank())
                JsValidationResult.Failure(error())
            else
                JsValidationResult.Success
        }

    fun <E> pattern(pattern: Regex, error: (value: String, pattern: Regex) -> E): JsValidator<String, E>
        where E : JsError =
        JsValidator { value ->
            if (pattern.matches(value))
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(value, pattern))
        }

    fun <E> isA(predicate: (String) -> Boolean, error: (value: String) -> JsError): JsValidator<String, JsError>
        where E : JsError =
        JsValidator { value ->
            if (predicate(value))
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(value))
        }
}
