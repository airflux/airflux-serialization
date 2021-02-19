package io.github.airflux.reader.validator

import io.github.airflux.reader.result.JsError

@Suppress("unused")
interface JsValidator<in T, out E : JsError> {

    companion object {
        operator fun <T, E : JsError> invoke(block: (T) -> JsValidationResult<E>) =
            object : JsValidator<T, E> {
                override fun validation(value: T): JsValidationResult<E> = block(value)
            }
    }

    fun validation(value: T): JsValidationResult<E>

    infix fun or(other: JsValidator<@UnsafeVariance T, @UnsafeVariance E>): JsValidator<T, E> {
        val self = this
        return JsValidator { value ->
            when (val result = self.validation(value)) {
                is JsValidationResult.Success -> result
                is JsValidationResult.Failure -> other.validation(value)
            }
        }
    }

    infix fun and(other: JsValidator<@UnsafeVariance T, @UnsafeVariance E>): JsValidator<T, E> {
        val self = this
        return JsValidator { value ->
            when (val result = self.validation(value)) {
                is JsValidationResult.Success -> other.validation(value)
                is JsValidationResult.Failure -> result
            }
        }
    }
}
