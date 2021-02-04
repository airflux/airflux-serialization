package io.github.airflux.reader

import io.github.airflux.dsl.CollectionBuilder
import io.github.airflux.dsl.CollectionBuilderFactory
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup
import io.github.airflux.value.extension.seek

/*
 Basic reads functions.
 |------------------------------------------------------------------------|
 |                   |              Found              |                  |
 |   Function name   |----------------|----------------|    Not found     |
 |                   |      JsNull    |     Other      |                  |
 |-------------------|----------------|----------------|------------------|
 | required          | applies reader | applies reader | PathMissingError |
 | orDefault         | default value  | applies reader | default value    |
 | nullable          | null           | applies reader | null             |
 | nullableOrDefault | null           | applies reader | default value    |
 |------------------------------------------------------------------------|
 */
@Suppress("unused")
interface PathReader {

    /**
     * Reads a required field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [JsError.PathMissing]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> required(path: JsPath, reader: JsReader<T>): JsReader<T> = JsReader { input ->
        input.seek(path)
            .flatMap { reader.read(it) }
            .repath(path)
    }

    /**
     * Reads a optional or nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [defaultValue]
     * - If the last node in [JsPath] is found with value 'null' then returns [defaultValue]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> orDefault(path: JsPath, reader: JsReader<T>, defaultValue: () -> T): JsReader<T> =
        nullable(path = path, reader = reader)
            .map { value -> value ?: defaultValue() }

    /**
     * Reads a optional or nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [null]
     * - If the last node in [JsPath] is found with value 'null' then returns [null]
     * - If the entire path is found then applies [reader]
     */
    fun <T> nullable(path: JsPath, reader: JsReader<T>): JsReader<T?> = JsReader { input ->
        when (val lookup = input.lookup(path)) {
            is JsLookup.Defined -> when (val node = lookup.value) {
                is JsNull -> JsResult.Success(value = null)
                else -> reader.read(node)
            }

            is JsLookup.Undefined -> JsResult.Success(value = null)
        }.repath(path)
    }

    /**
     * Reads a optional or nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [defaultValue]
     * - If the last node in [JsPath] is found with value 'null' then returns [null]
     * - If the entire path is found then applies [reader]
     */
    fun <T> nullableOrDefault(
        path: JsPath,
        reader: JsReader<T>,
        defaultValue: () -> T
    ): JsReader<T?> = JsReader { input ->
        when (val lookup = input.lookup(path)) {
            is JsLookup.Defined -> when (val node = lookup.value) {
                is JsNull -> JsResult.Success(value = null)
                else -> reader.read(node)
            }

            is JsLookup.Undefined -> JsResult.Success(value = defaultValue())
        }.repath(path)
    }

    /**
     * Reads a required and not nullable field which represent as array at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [JsError.PathMissing]
     * - If the entire path is found then applies [reader]
     */
    fun <T, C : Collection<T>> traversable(
        path: JsPath,
        reader: JsReader<T>,
        factory: CollectionBuilderFactory<T, C>
    ): JsReader<C> = JsReader { input ->
        input.seek(path)
            .flatMap { readCollection(it, reader, factory) }
            .repath(path)
    }

    private fun <T, C : Collection<T>> readCollection(
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
