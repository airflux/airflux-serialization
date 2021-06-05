package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError

@Suppress("unused")
fun interface JsValidator<in T, out E : JsError> {

    fun validation(context: JsReaderContext?, value: T): JsValidationResult<E>

    infix fun or(other: JsValidator<@UnsafeVariance T, @UnsafeVariance E>): JsValidator<T, E> {
        val self = this
        return JsValidator { context, value ->
            when (val result = self.validation(context, value)) {
                is JsValidationResult.Success -> result
                is JsValidationResult.Failure -> other.validation(context, value)
            }
        }
    }

    infix fun and(other: JsValidator<@UnsafeVariance T, @UnsafeVariance E>): JsValidator<T, E> {
        val self = this
        return JsValidator { context, value ->
            when (val result = self.validation(context, value)) {
                is JsValidationResult.Success -> other.validation(context, value)
                is JsValidationResult.Failure -> result
            }
        }
    }
}
