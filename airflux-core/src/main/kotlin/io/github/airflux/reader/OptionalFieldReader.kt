package io.github.airflux.reader

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

fun <T : Any> readOptional(
    from: JsLookup,
    using: JsReader<T>,
    invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
): JsResult<T?> =
    when (from) {
        is JsLookup.Defined -> using.read(from.value).repath(from.path)

        is JsLookup.Undefined.PathMissing -> JsResult.Success(path = from.path, value = null)

        is JsLookup.Undefined.InvalidType ->
            JsResult.Failure(path = from.path, error = invalidTypeErrorBuilder(from.expected, from.actual))
    }

/**
 * Reads nullable field at [path].
 *
 * - If any node in [path] is not found then returns 'null'
 * - If any node does not match path element type, then returning error [invalidTypeErrorBuilder]
 * - If the entire path is found then applies [reader]
 */
fun <T : Any> readOptional(
    from: JsValue,
    path: JsPath,
    using: JsReader<T>,
    invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
): JsResult<T?> =
    readOptional(from = from.lookup(path), using = using, invalidTypeErrorBuilder = invalidTypeErrorBuilder)

/**
 * Reads nullable field by [name].
 *
 * - If node is not found then returns 'null'
 * - If node is not object, then returning error [invalidTypeErrorBuilder]
 * - If the entire path is found then applies [reader]
 */
fun <T : Any> readOptional(
    from: JsValue,
    name: String,
    using: JsReader<T>,
    invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
): JsResult<T?> =
    readOptional(from = from.lookup(name), using = using, invalidTypeErrorBuilder = invalidTypeErrorBuilder)
