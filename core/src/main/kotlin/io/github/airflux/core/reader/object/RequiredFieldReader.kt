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

package io.github.airflux.core.reader.`object`

import io.github.airflux.core.context.error.get
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsResult

/**
 * Reads required field.
 *
 * - If a node is found ([from] is [JsLookup.Defined]) then applies [reader]
 * - If a node is not found ([from] is [JsLookup.Undefined.PathMissing]) then an error is returned
 *   that was build using [PathMissingErrorBuilder]
 * - If a node is not an object ([from] is [JsLookup.Undefined.InvalidType]) then an error is returned
 *   that was build using [InvalidTypeErrorBuilder]
 */
public fun <T : Any> readRequired(context: JsReaderContext, from: JsLookup, using: JsReader<T>): JsResult<T> =
    when (from) {
        is JsLookup.Defined -> using.read(context, from.location, from.value)
        is JsLookup.Undefined.PathMissing -> {
            val errorBuilder = context[PathMissingErrorBuilder]
            JsResult.Failure(location = from.location, error = errorBuilder.build())
        }
        is JsLookup.Undefined.InvalidType -> {
            val errorBuilder = context[InvalidTypeErrorBuilder]
            JsResult.Failure(location = from.location, error = errorBuilder.build(from.expected, from.actual))
        }
    }
