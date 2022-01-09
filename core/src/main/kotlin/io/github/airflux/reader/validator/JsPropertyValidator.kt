package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsErrors
import io.github.airflux.reader.result.JsLocation

@Suppress("unused")
fun interface JsPropertyValidator<in T> {

    fun validation(context: JsReaderContext, location: JsLocation, value: T): JsErrors?

    /*
     * | This | Other  | Result |
     * |------|--------|--------|
     * | S    | ignore | S      |
     * | F    | S      | S      |
     * | F    | F`     | F + F` |
     */
    infix fun or(other: JsPropertyValidator<@UnsafeVariance T>): JsPropertyValidator<T> {
        val self = this
        return JsPropertyValidator { context, location, value ->
            self.validation(context, location, value)
                ?.let { error ->
                    other.validation(context, location, value)
                        ?.let { error + it }
                }
        }
    }

    /*
     * | This | Other  | Result |
     * |------|--------|--------|
     * | S    | S      | S      |
     * | S    | F      | F      |
     * | F    | ignore | F      |
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
