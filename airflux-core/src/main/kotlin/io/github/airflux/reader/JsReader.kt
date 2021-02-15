package io.github.airflux.reader

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue

@Suppress("unused")
interface JsReader<T> {

    companion object : PathReader,
                       TraversableReader {

        operator fun <T> invoke(block: (JsValue) -> JsResult<T>) =
            object : JsReader<T> {
                override fun read(input: JsValue): JsResult<T> = block(input)
            }
    }

    /**
     * Convert the [JsValue] into a T
     */
    infix fun read(input: JsValue): JsResult<T>

    /**
     * Create a new [JsReader] which maps the value produced by this [JsReader].
     *
     * @param[R] The type of the value produced by the new [JsReader].
     * @param transform the function applied on the result of the current instance,
     * if successful
     * @return A new [JsReader] with the updated behavior.
     */
    infix fun <R> map(transform: (T) -> R): JsReader<R> =
        JsReader { input -> read(input).map(transform) }

    /**
     * Creates a new [JsReader], based on this one, which first executes this
     * [JsReader] logic then, if this [JsReader] resulted in a [JsError], runs
     * the other [JsReader] on the [JsValue].
     *
     * @param other the [JsReader] to run if this one gets a [JsError]
     * @return A new [JsReader] with the updated behavior.
     */
    infix fun or(other: JsReader<T>): JsReader<T> = JsReader { input ->
        when (val result = read(input)) {
            is JsResult.Success -> result
            is JsResult.Failure -> when (val alternative = other.read(input)) {
                is JsResult.Success -> alternative
                is JsResult.Failure -> {
                    JsResult.Failure(errors = result.errors + alternative.errors)
                }
            }
        }
    }
}
