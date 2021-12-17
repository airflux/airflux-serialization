package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsErrors
import io.github.airflux.reader.result.JsLocation

@Suppress("unused")
fun interface JsPropertyValidator<in T> {

    fun validation(context: JsReaderContext, location: JsLocation, value: T): JsErrors?

    /*
     * | This        | Other       | Result      |
     * |-------------|-------------|-------------|
     * | S           | ignore      | S           |
     * | F(critical) | ignore      | F(critical) |
     * | F(normal)   | S           | S           |
     * | F(normal)   | F`(normal)  | F + F`      |
     * | F(normal)   | F`(critical)| F + F`      |
     */
    infix fun or(other: JsPropertyValidator<@UnsafeVariance T>): JsPropertyValidator<T> {
        val self = this
        return JsPropertyValidator { context, location, value ->
            val result = self.validation(context, location, value)
            when {
                result == null -> null
                result.hasCritical() -> result
                else -> other.validation(context, location, value)?.let { result + it }
            }
        }
    }

    /*
     * | This       | Other      | Result |
     * |------------|------------|--------|
     * | S          | S          | S      |
     * | S          | F(critical)| F      |
     * | S          | F(normal)  | F      |
     * | F(critical)| ignore     | F      |
     * | F(normal)  | ignore     | F      |
     */
    infix fun and(other: JsPropertyValidator<@UnsafeVariance T>): JsPropertyValidator<T> {
        val self = this
        return JsPropertyValidator { context, location, value ->
            when (val result = self.validation(context, location, value)) {
                null -> other.validation(context, location, value)
                else -> result
            }
        }
    }
}
