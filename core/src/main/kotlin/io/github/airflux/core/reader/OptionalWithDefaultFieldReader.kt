/*
 * Copyright 2021-2022 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import io.github.airflux.core.reader.result.JsResult

/**
 * Reads optional field or return default if a field is not found.
 *
 * - If a node is found ([from] is [JsLookup.Defined]) then applies [reader]
 * - If a node is not found ([from] is [JsLookup.Undefined.PathMissing]) then returns [defaultValue]
 * - If a node is not an object ([from] is [JsLookup.Undefined.InvalidType]) then returning error [invalidTypeErrorBuilder]
 */
fun <T : Any> readOptional(
    context: JsReaderContext,
    from: JsLookup,
    using: JsReader<T>,
    defaultValue: () -> T,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder
): JsResult<T> = when (from) {
    is JsLookup.Defined -> using.read(context, from.location, from.value)
    is JsLookup.Undefined.PathMissing -> JsResult.Success(location = from.location, value = defaultValue())
    is JsLookup.Undefined.InvalidType ->
        JsResult.Failure(location = from.location, error = invalidTypeErrorBuilder.build(from.expected, from.actual))
}
