package io.github.airflux.reader

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsValue

/**
 * Read a node as list.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any> readAsList(
    context: JsReaderContext,
    location: JsLocation,
    from: JsValue,
    using: JsReader<T>,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<List<T>> =
    readAsCollection(context, location, from, using, CollectionBuilderFactory.listFactory(), invalidTypeErrorBuilder)

/**
 * Read a node as set.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any> readAsSet(
    context: JsReaderContext,
    location: JsLocation,
    from: JsValue,
    using: JsReader<T>,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<Set<T>> =
    readAsCollection(context, location, from, using, CollectionBuilderFactory.setFactory(), invalidTypeErrorBuilder)

/**
 * Read a node which represent as array.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any, C> readAsCollection(
    context: JsReaderContext,
    location: JsLocation,
    from: JsValue,
    using: JsReader<T>,
    factory: CollectionBuilderFactory<T, C>,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<C>
    where C : Collection<T> = when (from) {
    is JsArray<*> -> {
        val values = factory.newBuilder(from.size)
        val initial: JsResult<CollectionBuilder<T, C>> = JsResult.Success(value = values, location = location)
        from.withIndex()
            .fold(initial) { acc, (idx, elem) ->
                when (val result = using.read(context, location / idx, elem)) {
                    is JsResult.Success<T> -> when (acc) {
                        is JsResult.Success<*> -> acc.also { values += result.value }
                        is JsResult.Failure -> acc as JsResult<CollectionBuilder<T, C>>
                    }

                    is JsResult.Failure -> when (acc) {
                        is JsResult.Success<*> -> result
                        is JsResult.Failure -> acc + result
                    }
                }
            }
            .map { it.result() }
    }

    else -> JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.ARRAY, from.type))
}
