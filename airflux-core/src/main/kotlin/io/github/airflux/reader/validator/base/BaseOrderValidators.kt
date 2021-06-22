package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.JsValidationResult

@Suppress("unused")
object BaseOrderValidators {

    /**
     * Validation of a value, if a value less than a [expected] value then [error], otherwise success.
     */
    fun <T, E> min(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value < expected)
                JsValidationResult.Failure(error(expected, value))
            else
                JsValidationResult.Success
        }

    /**
     * Validation of a value, if a value more than a [expected] value then [error], otherwise success.
     */
    fun <T, E> max(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value > expected)
                JsValidationResult.Failure(error(expected, value))
            else
                JsValidationResult.Success
        }

    /**
     * Validation of a value, if a value equal to a [expected] value then success, otherwise [error].
     */
    fun <T, E> eq(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value == expected)
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(expected, value))
        }

    /**
     * Validation of a value, if a value not equal to a [expected] value then success, otherwise [error].
     */
    fun <T, E> ne(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value != expected)
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(expected, value))
        }

    /**
     * Validation of a value, if a value greater than a [expected] value then success, otherwise [error].
     */
    fun <T, E> gt(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value > expected)
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(expected, value))
        }

    /**
     * Validation of a value, if a value greater than or equal to a [expected] value then success, otherwise [error].
     */
    fun <T, E> ge(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value >= expected)
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(expected, value))
        }

    /**
     * Validation of a value, if a value less than a [expected] value then success, otherwise [error].
     */
    fun <T, E> lt(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value < expected)
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(expected, value))
        }

    /**
     * Validation of a value, if a value less than or equal to a [expected] value then success, otherwise [error].
     */
    fun <T, E> le(expected: T, error: (expected: T, actual: T) -> E): JsPropertyValidator<T, E>
        where T : Comparable<T>,
              E : JsError =
        JsPropertyValidator { _, _, value ->
            if (value <= expected)
                JsValidationResult.Success
            else
                JsValidationResult.Failure(error(expected, value))
        }
}
