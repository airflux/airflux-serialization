package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError

@Suppress("unused")
fun interface JsValidator<in T, out E : JsError> {

    fun validation(value: T, context: JsReaderContext?): JsValidationResult<E>

    infix fun or(other: JsValidator<@UnsafeVariance T, @UnsafeVariance E>): JsValidator<T, E> {
        val self = this
        return JsValidator { value, context ->
            when (val result = self.validation(value, context)) {
                is JsValidationResult.Success -> result
                is JsValidationResult.Failure -> other.validation(value, context)
            }
        }
    }

    infix fun and(other: JsValidator<@UnsafeVariance T, @UnsafeVariance E>): JsValidator<T, E> {
        val self = this
        return JsValidator { value, context ->
            when (val result = self.validation(value, context)) {
                is JsValidationResult.Success -> other.validation(value, context)
                is JsValidationResult.Failure -> result
            }
        }
    }
}
