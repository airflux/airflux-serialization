package io.github.airflux.reader

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

/**
 * Reads a required field at [JsPath].
 */
@Suppress("unused")
interface RequiredPathReader {

    companion object {

        fun <T : Any> required(
            from: JsLookup,
            using: JsReader<T>,
            errorPathMissing: () -> JsError,
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
        ): JsResult<T> =
            when (from) {
                is JsLookup.Defined -> using.read(from.value).repath(from.path)

                is JsLookup.Undefined.PathMissing ->
                    JsResult.Failure(path = from.path, error = errorPathMissing())

                is JsLookup.Undefined.InvalidType ->
                    JsResult.Failure(path = from.path, error = errorInvalidType(from.expected, from.actual))
            }

        /**
         * Reads a required field at [JsPath].
         *
         * - If any node in [JsPath] is not found then returns error [errorPathMissing]
         * - If any node does not match path element type, then returning error [errorInvalidType]
         * - If the entire path is found then applies [reader]
         */
        fun <T : Any> required(
            from: JsValue,
            path: JsPath,
            using: JsReader<T>,
            errorPathMissing: () -> JsError,
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
        ): JsResult<T> =
            required(
                from = from.lookup(path),
                using = using,
                errorPathMissing = errorPathMissing,
                errorInvalidType = errorInvalidType
            )

        fun <T : Any> required(
            from: JsValue,
            name: String,
            using: JsReader<T>,
            errorPathMissing: () -> JsError,
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
        ): JsResult<T> =
            required(
                from = from.lookup(name),
                using = using,
                errorPathMissing = errorPathMissing,
                errorInvalidType = errorInvalidType
            )
    }
}
