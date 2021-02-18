package io.github.airflux.reader

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

/**
 * Reads nullable field at [JsPath].
 */
@Suppress("unused")
interface NullablePathReader {

    companion object {

        fun <T : Any> nullable(from: JsLookup, using: JsReader<T>): JsResult<T?> =
            when (from) {
                is JsLookup.Defined -> when (from.value) {
                    is JsNull -> JsResult.Success(path = from.path, value = null)
                    else -> using.read(from.value).repath(from.path)
                }

                is JsLookup.Undefined.PathMissing -> JsResult.Success(path = from.path, value = null)

                is JsLookup.Undefined.InvalidType ->
                    JsResult.Failure(path = from.path, error = JsError.InvalidType(from.expected, from.actual))
            }

        /**
         * Reads nullable field at [JsPath].
         *
         * - If any node in [JsPath] is not found then returns 'null'
         * - If the last node in [JsPath] is found with value 'null' then returns 'null'
         * - If any node does not match path element type, then returning error [JsError.InvalidType]
         * - If the entire path is found then applies [reader]
         */
        fun <T : Any> nullable(from: JsValue, path: JsPath, using: JsReader<T>): JsResult<T?> =
            nullable(from = from.lookup(path), using = using)
    }
}
