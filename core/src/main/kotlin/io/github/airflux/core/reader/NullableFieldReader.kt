/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.core.reader

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull

/**
 * Reads nullable field or return default if a field is not found.
 *
 * - If a node is found with a value no 'null' ([from] is [JsLookup.Defined]) then applies [reader]
 * - If a node is found with a value 'null' ([from] is [JsLookup.Defined]) then returns 'null'
 * - If a node is not found ([from] is [JsLookup.Undefined.PathMissing]) then returning error [invalidTypeErrorBuilder]
 * - If a node is not an object ([from] is [JsLookup.Undefined.InvalidType]) then returning error [invalidTypeErrorBuilder]
 */
fun <T : Any> readNullable(
    context: JsReaderContext,
    from: JsLookup,
    using: JsReader<T>,
    pathMissingErrorBuilder: PathMissingErrorBuilder,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T?> {

    fun <T : Any> readNullable(context: JsReaderContext, from: JsLookup.Defined, using: JsReader<T>): JsResult<T?> =
        when (from.value) {
            is JsNull -> JsResult.Success(location = from.location, value = null)
            else -> using.read(context, from.location, from.value)
        }

    return when (from) {
        is JsLookup.Defined -> readNullable(context, from, using)
        is JsLookup.Undefined.PathMissing ->
            JsResult.Failure(location = from.location, error = pathMissingErrorBuilder.build())
        is JsLookup.Undefined.InvalidType -> JsResult.Failure(
            location = from.location,
            error = invalidTypeErrorBuilder.build(from.expected, from.actual)
        )
    }
}
