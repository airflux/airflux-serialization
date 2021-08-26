package io.github.airflux.reader

import io.github.airflux.lookup.JsLookup
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull

/**
 * Reads nullable field or return default if a field is not found.
 *
 * - If a node is found with a value no 'null' ([from] is [JsLookup.Defined]) then applies [reader]
 * - If a node is found with a value 'null' ([from] is [JsLookup.Defined]) then returns 'null'
 * - If a node is not found ([from] is [JsLookup.Undefined.PathMissing]) then returns [defaultValue]
 * - If a node is not an object ([from] is [JsLookup.Undefined.InvalidType]) then returning error [invalidTypeErrorBuilder]
 */
fun <T : Any> readNullable(
    context: JsReaderContext,
    from: JsLookup,
    using: JsReader<T>,
    defaultValue: () -> T?,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T?> = when (from) {
    is JsLookup.Defined -> when (from.value) {
        is JsNull -> JsResult.Success(path = from.path, value = null)
        else -> using.read(context, from.path, from.value)
    }

    is JsLookup.Undefined.PathMissing -> JsResult.Success(path = from.path, value = defaultValue())

    is JsLookup.Undefined.InvalidType ->
        JsResult.Failure(path = from.path, error = invalidTypeErrorBuilder.build(from.expected, from.actual))
}
