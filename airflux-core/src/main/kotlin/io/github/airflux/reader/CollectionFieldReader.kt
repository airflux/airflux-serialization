package io.github.airflux.reader

import io.github.airflux.path.JsPath
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsValue

/**
 * Read a node as list.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any> readAsList(
    from: JsValue,
    using: JsReader<T>,
    context: JsReaderContext?,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<List<T>> =
    readAsCollection(from, using, CollectionBuilderFactory.listFactory(), context, invalidTypeErrorBuilder)

/**
 * Read a node as set.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any> readAsSet(
    from: JsValue,
    using: JsReader<T>,
    context: JsReaderContext?,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<Set<T>> =
    readAsCollection(from, using, CollectionBuilderFactory.setFactory(), context, invalidTypeErrorBuilder)

/**
 * Read a node which represent as array.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any, C> readAsCollection(
    from: JsValue,
    using: JsReader<T>,
    factory: CollectionBuilderFactory<T, C>,
    context: JsReaderContext?,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<C>
    where C : Collection<T> =
    when (from) {
        is JsArray<*> -> {
            val values = factory.newBuilder(from.underlying.size)
            from.underlying
                .withIndex()
                .fold(JsResult.Success(values)) { acc: JsResult<CollectionBuilder<T, C>>, (idx, elem) ->
                    when (val result = using.read(context, elem)) {
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

        else -> JsResult.Failure(error = invalidTypeErrorBuilder.build(JsValue.Type.ARRAY, from.type))
    }
