/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.lookup.JsLookupResult
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success

/**
 * Reads optional property.
 *
 * - If a node is found (receiver is [JsLookupResult.Defined]) then applies [reader]
 * - If a node is not found (receiver is [JsLookupResult.Undefined]) then returns 'null'
 */
public fun <EB, O, T> JsLookupResult.readOptional(
    env: JsReaderEnv<EB, O>,
    using: JsReader<EB, O, T>
): JsReaderResult<T?>
    where EB : InvalidTypeErrorBuilder =
    when (this) {
        is JsLookupResult.Defined -> using.read(env, location, value)
        is JsLookupResult.Undefined -> when (this) {
            is JsLookupResult.Undefined.PathMissing -> success(location = location, value = null)

            is JsLookupResult.Undefined.InvalidType -> failure(
                location = location,
                error = env.config.errorBuilders.invalidTypeError(expected = expected, actual = actual)
            )
        }
    }
