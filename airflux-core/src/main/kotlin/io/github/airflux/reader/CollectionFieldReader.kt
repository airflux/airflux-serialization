package io.github.airflux.reader

import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsValue

/**
 * Read a node as list.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any, E : JsError> readAsList(
    from: JsValue,
    using: JsReader<T, E>,
    invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
): JsResult<List<T>, E> =
    readAsCollection(from, using, CollectionBuilderFactory.listFactory(), invalidTypeErrorBuilder)

/**
 * Read a node as set.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any, E : JsError> readAsSet(
    from: JsValue,
    using: JsReader<T, E>,
    invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
): JsResult<Set<T>, E> =
    readAsCollection(from, using, CollectionBuilderFactory.setFactory(), invalidTypeErrorBuilder)

/**
 * Read a node which represent as array.
 *
 * - If node does not match array type, then returning error [invalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any, E : JsError, C> readAsCollection(
    from: JsValue,
    using: JsReader<T, E>,
    factory: CollectionBuilderFactory<T, C>,
    invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
): JsResult<C, E>
    where C : Collection<T> =
    when (from) {
        is JsArray<*> -> {
            val values = factory.newBuilder(from.underlying.size)
            from.underlying
                .withIndex()
                .fold(JsResult.Success(values)) { acc: JsResult<CollectionBuilder<T, C>, E>, (idx, elem) ->
                    when (val result = using.read(elem)) {
                        is JsResult.Success<T> -> {
                            when (acc) {
                                is JsResult.Success<*> -> acc.also { values += result.value }
                                is JsResult.Failure -> acc as JsResult<CollectionBuilder<T, C>, E>
                            }
                        }
                        is JsResult.Failure<E> -> when (acc) {
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

        else -> JsResult.Failure(error = invalidTypeErrorBuilder(JsValue.Type.ARRAY, from.type))
    }
