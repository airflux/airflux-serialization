package io.github.airflux.core.reader

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsValue

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
    where C : Collection<T> {

    fun <T, C : Collection<T>> readAsCollection(
        context: JsReaderContext,
        location: JsLocation,
        from: JsArray<*>,
        using: JsReader<T>,
        factory: CollectionBuilderFactory<T, C>
    ): JsResult<C> {

        fun <T, C : Collection<T>> dispatch(
            result: JsResult.Success<T>,
            acc: JsResult<CollectionBuilder<T, C>>
        ): JsResult<CollectionBuilder<T, C>> =
            when (acc) {
                is JsResult.Success<CollectionBuilder<T, C>> -> acc.apply { value += result.value }
                is JsResult.Failure -> acc
            }

        fun <T, C : Collection<T>> dispatch(
            result: JsResult.Failure,
            acc: JsResult<CollectionBuilder<T, C>>
        ): JsResult<CollectionBuilder<T, C>> =
            when (acc) {
                is JsResult.Success<CollectionBuilder<T, C>> -> result
                is JsResult.Failure -> result + acc
            }

        val values = factory.newBuilder(from.size)
        val initial: JsResult<CollectionBuilder<T, C>> = JsResult.Success(value = values, location = location)
        return from.foldIndexed(initial) { idx, acc, elem ->
            when (val result = using.read(context, location.append(idx), elem)) {
                is JsResult.Success<T> -> dispatch(result, acc)
                is JsResult.Failure -> dispatch(result, acc)
            }
        }.map { it.result() }
    }

    return when (from) {
        is JsArray<*> -> readAsCollection(context, location, from, using, factory)
        else -> JsResult.Failure(location, invalidTypeErrorBuilder.build(JsValue.Type.ARRAY, from.type))
    }
}
