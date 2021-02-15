package io.github.airflux.reader

import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsValue

@Suppress("unused")
interface TraversableReader {

    /**
     * Reads a field which represent as array.
     *
     * - If [input] does not match the array type, then returning [JsError.InvalidType]
     * - If the entire path is found then applies [reader]
     */
    fun <T, C : Collection<T>> traversable(
        input: JsValue,
        reader: JsReader<T>,
        factory: CollectionBuilderFactory<T, C>
    ): JsResult<C> = when (input) {
        is JsArray<*> -> {
            val values = factory.newBuilder(input.underlying.size)
            input.underlying
                .withIndex()
                .fold(JsResult.Success(values)) { acc: JsResult<CollectionBuilder<T, C>>, (idx, elem) ->
                    when (val result = reader.read(elem)) {
                        is JsResult.Success<T> -> {
                            when (acc) {
                                is JsResult.Success<*> -> acc.also { values += result.value }
                                is JsResult.Failure -> acc as JsResult<CollectionBuilder<T, C>>
                            }
                        }
                        is JsResult.Failure -> when (acc) {
                            is JsResult.Success<*> -> result.repath(JsPath(idx))
                            is JsResult.Failure -> {
                                val accErr = acc.errors
                                val newErr = JsPath.repath(path = JsPath(idx), errors = result.errors)
                                JsResult.Failure(accErr + newErr)
                            }
                        }
                    }
                }
                .map { it.result() }
        }

        else -> JsResult.Failure(error = JsError.InvalidType(expected = JsValue.Type.ARRAY, actual = input.type))
    }
}
