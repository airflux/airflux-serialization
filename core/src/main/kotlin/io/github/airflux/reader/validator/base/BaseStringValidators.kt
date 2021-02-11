package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

@Suppress("unused")
object BaseStringValidators {

    fun <E> minLength(error: (expected: Int, actual: Int) -> E): (Int) -> JsValidator<String, E>
        where E : JsError.Validation =
        { expected ->
            JsValidator { value ->
                if (value.length < expected)
                    JsValidationResult.Failure(error(expected, value.length))
                else
                    JsValidationResult.Success
            }
        }

    fun <E> maxLength(error: (expected: Int, actual: Int) -> E): (Int) -> JsValidator<String, E>
        where E : JsError.Validation =
        { expected ->
            JsValidator { value ->
                if (value.length > expected)
                    JsValidationResult.Failure(error(expected, value.length))
                else
                    JsValidationResult.Success
            }
        }

    fun <E> isNotEmpty(error: () -> E)
        where E : JsError.Validation =
        JsValidator<String, E> { value ->
            if (value.isEmpty())
                JsValidationResult.Failure(error())
            else
                JsValidationResult.Success
        }

    fun <E> isNotBlank(error: () -> E)
        where E : JsError.Validation =
        JsValidator<String, E> { value ->
            if (value.isBlank())
                JsValidationResult.Failure(error())
            else
                JsValidationResult.Success
        }

    fun <E> pattern(error: (value: String, pattern: Regex) -> E): (Regex) -> JsValidator<String, E>
        where E : JsError.Validation =
        { pattern ->
            JsValidator { value ->
                if (pattern.matches(value))
                    JsValidationResult.Success
                else
                    JsValidationResult.Failure(error(value, pattern))
            }
        }

    fun <E> isA(error: (value: String) -> E): ((value: String) -> Boolean) -> JsValidator<String, E>
        where E : JsError.Validation =
        { predicate ->
            JsValidator { value ->
                if (predicate(value))
                    JsValidationResult.Success
                else
                    JsValidationResult.Failure(error(value))
            }
        }
}
