package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResultPath

@Suppress("unused")
fun interface JsPropertyValidator<in T> {

    fun validation(context: JsReaderContext, path: JsResultPath, value: T): List<JsError>

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
        return JsPropertyValidator { context, path, value ->
            val result = self.validation(context, path, value)
            when {
                result.isSuccess() -> emptyList()
                result.hasCritical() -> result
                else -> {
                    val otherResult = other.validation(context, path, value)
                    if (otherResult.isSuccess())
                        emptyList()
                    else
                        result + otherResult
                }
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
        fun <E : JsError> List<E>.isFailure() = this.isNotEmpty()
        fun <E : JsError> List<E>.hasCritical() = any { it.level == JsError.Level.CRITICAL }
    }
}
