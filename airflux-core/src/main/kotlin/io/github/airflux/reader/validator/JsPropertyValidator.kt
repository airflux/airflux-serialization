package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResultPath

@Suppress("unused")
fun interface JsPropertyValidator<in T> {

    fun validation(context: JsReaderContext, path: JsResultPath, value: T): List<JsError>

    infix fun or(other: JsPropertyValidator<@UnsafeVariance T>): JsPropertyValidator<T> {
        val self = this
        return JsPropertyValidator { context, path, value ->
            val result = self.validation(context, path, value)
            when {
                result.isSuccess() -> result
                else -> other.validation(context, path, value)
            }
        }
    }

    infix fun and(other: JsPropertyValidator<@UnsafeVariance T>): JsPropertyValidator<T> {
        val self = this
        return JsPropertyValidator { context, path, value ->
            val result = self.validation(context, path, value)
            when {
                result.isSuccess() -> other.validation(context, path, value)
                else -> result
            }
        }
    }

    companion object {
        fun <E : JsError> List<E>.isSuccess() = this.isEmpty()
    }
}
