package io.github.airflux.reader

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
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
    currentPath: JsResultPath,
    from: JsValue,
    using: JsReader<T>,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<List<T>> =
    readAsCollection(context, currentPath, from, using, CollectionBuilderFactory.listFactory(), invalidTypeErrorBuilder)

/**
 * Read a node as set.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any> readAsSet(
    context: JsReaderContext,
    currentPath: JsResultPath,
    from: JsValue,
    using: JsReader<T>,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<Set<T>> =
    readAsCollection(context, currentPath, from, using, CollectionBuilderFactory.setFactory(), invalidTypeErrorBuilder)

/**
 * Read a node which represent as array.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any, C> readAsCollection(
    context: JsReaderContext,
    currentPath: JsResultPath,
    from: JsValue,
    using: JsReader<T>,
    factory: CollectionBuilderFactory<T, C>,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<C>
    where C : Collection<T> = when (from) {
    is JsArray<*> -> {
        val values = factory.newBuilder(from.underlying.size)
        val initial: JsResult<CollectionBuilder<T, C>> = JsResult.Success(value = values, path = currentPath)
        from.underlying
            .withIndex()
            .fold(initial) { acc, (idx, elem) ->
                when (val result = using.read(context, currentPath / idx, elem)) {
                    is JsResult.Success<T> -> when (acc) {
                        is JsResult.Success<*> -> acc.also { values += result.value }
                        is JsResult.Failure -> acc as JsResult<CollectionBuilder<T, C>>
                    }

                    is JsResult.Failure -> when (acc) {
                        is JsResult.Success<*> -> result/*.repath(JsPath(idx))*/
                        is JsResult.Failure -> JsResult.Failure(acc.errors + result.errors)
                    }
                }
            }
            .map { it.result() }
    }

    else -> JsResult.Failure(path = currentPath, error = invalidTypeErrorBuilder.build(JsValue.Type.ARRAY, from.type))
}
