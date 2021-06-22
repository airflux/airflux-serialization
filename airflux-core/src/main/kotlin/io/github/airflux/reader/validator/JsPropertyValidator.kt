package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResultPath

@Suppress("unused")
fun interface JsPropertyValidator<in T, out E : JsError> {

    fun validation(context: JsReaderContext?, path: JsResultPath, value: T): JsValidationResult<E>

    infix fun or(other: JsPropertyValidator<@UnsafeVariance T, @UnsafeVariance E>): JsPropertyValidator<T, E> {
        val self = this
        return JsPropertyValidator { context, path, value ->
            when (val result = self.validation(context, path, value)) {
                is JsValidationResult.Success -> result
                is JsValidationResult.Failure -> other.validation(context, path, value)
            }
        }
    }

    infix fun and(other: JsPropertyValidator<@UnsafeVariance T, @UnsafeVariance E>): JsPropertyValidator<T, E> {
        val self = this
        return JsPropertyValidator { context, path, value ->
            when (val result = self.validation(context, path, value)) {
                is JsValidationResult.Success -> other.validation(context, path, value)
                is JsValidationResult.Failure -> result
            }
        }
    }
}
