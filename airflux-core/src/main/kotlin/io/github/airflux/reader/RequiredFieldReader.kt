package io.github.airflux.reader

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

fun <T : Any> readRequired(
    from: JsLookup,
    using: JsReader<T>,
    context: JsReaderContext?,
    pathMissingErrorBuilder: PathMissingErrorBuilder,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T> =
    when (from) {
        is JsLookup.Defined -> using.read(context, from.value).repath(from.path)

        is JsLookup.Undefined.PathMissing ->
            JsResult.Failure(path = from.path, error = pathMissingErrorBuilder.build())

        is JsLookup.Undefined.InvalidType ->
            JsResult.Failure(path = from.path, error = invalidTypeErrorBuilder.build(from.expected, from.actual))
    }

/**
 * Reads a required field at [path].
 *
 * - If any node in [path] is not found then returns error [pathMissingErrorBuilder]
 * - If any node does not match path element type, then returning error [invalidTypeErrorBuilder]
 * - If the entire path is found then applies [reader]
 */
fun <T : Any> readRequired(
    from: JsValue,
    path: JsPath,
    using: JsReader<T>,
    context: JsReaderContext?,
    pathMissingErrorBuilder: PathMissingErrorBuilder,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T> =
    readRequired(
        from = from.lookup(path),
        using = using,
        context = context,
        pathMissingErrorBuilder = pathMissingErrorBuilder,
        invalidTypeErrorBuilder = invalidTypeErrorBuilder
    )

/**
 * Reads a required field by [name].
 *
 * - If node by name is not found then returns error [pathMissingErrorBuilder]
 * - If node is not object, then returning error [invalidTypeErrorBuilder]
 * - If the entire path is found then applies [reader]
 */
fun <T : Any> readRequired(
    from: JsValue,
    name: String,
    using: JsReader<T>,
    context: JsReaderContext?,
    pathMissingErrorBuilder: PathMissingErrorBuilder,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T> =
    readRequired(
        from = from.lookup(name),
        using = using,
        context = context,
        pathMissingErrorBuilder = pathMissingErrorBuilder,
        invalidTypeErrorBuilder = invalidTypeErrorBuilder
    )
