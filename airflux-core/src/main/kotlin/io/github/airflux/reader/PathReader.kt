package io.github.airflux.reader

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull
import io.github.airflux.value.extension.lookup

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
     * - If any node does not match path element type, then returning [JsError.InvalidType]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> required(path: JsPath, reader: JsReader<T>): JsReader<T> = JsReader { input ->
        when (val result = input.lookup(path)) {
            is JsLookup.Defined ->
                reader.read(result.value).repath(path)

            is JsLookup.Undefined.PathMissing ->
                JsResult.Failure(path = path, error = JsError.PathMissing)

            is JsLookup.Undefined.InvalidType ->
                JsResult.Failure(
                    path = result.path,
                    error = JsError.InvalidType(expected = result.expected, actual = result.actual)
                )
        }
    }

    /**
     * Reads required field at [JsPath] or return default if a field is not found.
     *
     * - If any node in [JsPath] is not found then returns [defaultValue]
     * - If the last node in [JsPath] is found with value 'null' then returns [defaultValue]
     * - If any node does not match path element type, then returning [JsError.InvalidType]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> orDefault(path: JsPath, reader: JsReader<T>, defaultValue: () -> T): JsReader<T> =
        nullable(path = path, reader = reader)
            .map { value -> value ?: defaultValue() }

    /**
     * Reads nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns 'null'
     * - If the last node in [JsPath] is found with value 'null' then returns 'null'
     * - If any node does not match path element type, then returning [JsError.InvalidType]
     * - If the entire path is found then applies [reader]
     */
    fun <T> nullable(path: JsPath, reader: JsReader<T>): JsReader<T?> = JsReader { input ->
        when (val lookup = input.lookup(path)) {
            is JsLookup.Defined -> when (val node = lookup.value) {
                is JsNull -> JsResult.Success(path = path, value = null)
                else -> reader.read(node).repath(path)
            }

            is JsLookup.Undefined.PathMissing -> JsResult.Success(path = path, value = null)

            is JsLookup.Undefined.InvalidType ->
                JsResult.Failure(
                    path = lookup.path,
                    error = JsError.InvalidType(expected = lookup.expected, actual = lookup.actual)
                )
        }
    }

    /**
     * Reads nullable field at [JsPath] or return default if a field is not found.
     *
     * - If any node in [JsPath] is not found then returns [defaultValue]
     * - If the last node in [JsPath] is found with value 'null' then returns 'null'
     * - If any node does not match path element type, then returning [JsError.InvalidType]
     * - If the entire path is found then applies [reader]
     */
    fun <T> nullableOrDefault(path: JsPath, reader: JsReader<T>, defaultValue: () -> T): JsReader<T?> =
        JsReader { input ->
            when (val lookup = input.lookup(path)) {
                is JsLookup.Defined -> when (val node = lookup.value) {
                    is JsNull -> JsResult.Success(path = path, value = null)
                    else -> reader.read(node).repath(path)
                }

                is JsLookup.Undefined.PathMissing ->
                    JsResult.Success(path = path, value = defaultValue())

                is JsLookup.Undefined.InvalidType ->
                    JsResult.Failure(
                        path = lookup.path,
                        error = JsError.InvalidType(expected = lookup.expected, actual = lookup.actual)
                    )
            }
        }
}
