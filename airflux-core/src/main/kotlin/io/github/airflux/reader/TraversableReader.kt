package io.github.airflux.reader

import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsValue

@Suppress("unused")
interface TraversableReader {

    companion object {

        /**
         * Build of reader for read a node as list.
         *
         * - If node does not match array type, then returning error.
         * - If node match array type, then applies [using]
         */
        fun <T : Any> list(
            using: JsReader<T>,
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
        ): JsReader<List<T>> =
            JsReader { input ->
                traversable(
                    from = input,
                    using = using,
                    factory = CollectionBuilderFactory.listFactory(),
                    errorInvalidType = errorInvalidType
                )
            }

        /**
         * Build of reader for read a node as set.
         *
         * - If node does not match array type, then returning error.
         * - If node match array type, then applies [using]
         */
        fun <T : Any> set(
            using: JsReader<T>,
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
        ): JsReader<Set<T>> =
            JsReader { input ->
                traversable(
                    from = input,
                    using = using,
                    factory = CollectionBuilderFactory.setFactory(),
                    errorInvalidType = errorInvalidType
                )
            }

        /**
         * Reads a node which represent as array.
         *
         * - If node does not match array type, then returning error.
         * - If node match array type, then applies [using]
         */
        fun <T : Any, C> traversable(
            from: JsValue,
            using: JsReader<T>,
            factory: CollectionBuilderFactory<T, C>,
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
        ): JsResult<C>
            where C : Collection<T> =
            when (from) {
                is JsArray<*> -> {
                    val values = factory.newBuilder(from.underlying.size)
                    from.underlying
                        .withIndex()
                        .fold(JsResult.Success(values)) { acc: JsResult<CollectionBuilder<T, C>>, (idx, elem) ->
                            when (val result = using.read(elem)) {
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

                else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.ARRAY, from.type))
            }
    }
}
