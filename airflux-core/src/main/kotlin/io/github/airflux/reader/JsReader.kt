package io.github.airflux.reader

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsValue

@Suppress("unused")
fun interface JsReader<T> {

    /**
     * Convert the [JsValue] into a T
     */
    fun read(context: JsReaderContext, path: JsResultPath, input: JsValue): JsResult<T>

    /**
     * Create a new [JsReader] which maps the value produced by this [JsReader].
     *
     * @param[R] The type of the value produced by the new [JsReader].
     * @param transform the function applied on the result of the current instance,
     * if successful
     * @return A new [JsReader] with the updated behavior.
     */
    infix fun <R> map(transform: (T) -> R): JsReader<R> =
        JsReader { context, path, input -> read(context, path, input).map(transform) }

    /**
     * Creates a new [JsReader], based on this one, which first executes this
     * [JsReader] logic then, if this [JsReader] resulted in a [JsError], runs
     * the other [JsReader] on the [JsValue].
     *
     * @param other the [JsReader] to run if this one gets a [JsError]
     * @return A new [JsReader] with the updated behavior.
     */
    infix fun or(other: JsReader<T>): JsReader<T> = JsReader { context, path, input ->
        when (val result = read(context, path, input)) {
            is JsResult.Success -> result
            is JsResult.Failure -> when (val alternative = other.read(context, path, input)) {
                is JsResult.Success -> alternative
                is JsResult.Failure -> result + alternative
            }
        }
    }
}
