package io.github.airflux.core.reader

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull

/**
 * Reads required field or return default if a field is not found.
 *
 * - If a node is found with a value no 'null' ([from] is [JsLookup.Defined]) then applies [reader]
 * - If a node is found with a value 'null' ([from] is [JsLookup.Defined]) then returns [defaultValue]
 * - If a node is not found ([from] is [JsLookup.Undefined.PathMissing]) then returns [defaultValue]
 * - If a node is not an object ([from] is [JsLookup.Undefined.InvalidType]) then returning error [invalidTypeErrorBuilder]
 */
fun <T : Any> readWithDefault(
    context: JsReaderContext,
    from: JsLookup,
    using: JsReader<T>,
    defaultValue: () -> T,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T> {

    fun <T : Any> readWithDefault(
        context: JsReaderContext,
        from: JsLookup.Defined,
        using: JsReader<T>,
        defaultValue: () -> T
    ): JsResult<T> = when (from.value) {
        is JsNull -> JsResult.Success(location = from.location, value = defaultValue())
        else -> using.read(context, from.location, from.value)
    }

    return when (from) {
        is JsLookup.Defined -> readWithDefault(context, from, using, defaultValue)
        is JsLookup.Undefined.PathMissing -> JsResult.Success(location = from.location, value = defaultValue())
        is JsLookup.Undefined.InvalidType -> JsResult.Failure(
            location = from.location,
            error = invalidTypeErrorBuilder.build(from.expected, from.actual)
        )
    }
}
