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
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure

/**
 * Reads required property.
 *
 * - If a node is found ([lookup] is [JsLookupResult.Defined]) then applies [reader]
 * - If a node is not found ([lookup] is [JsLookupResult.Undefined]) then an error is returned
 *   that was build using [PathMissingErrorBuilder]
 */
public fun <EB, O, T> readRequired(
    env: JsReaderEnv<EB, O>,
    lookup: JsLookupResult,
    using: JsReader<EB, O, T>
): JsReaderResult<T>
    where EB : PathMissingErrorBuilder,
          EB : InvalidTypeErrorBuilder =
    when (lookup) {
        is JsLookupResult.Defined -> using.read(env, lookup.location, lookup.value)

        is JsLookupResult.Undefined -> when (lookup) {
            is JsLookupResult.Undefined.PathMissing ->
                failure(location = lookup.location, error = env.config.errorBuilders.pathMissingError())

            is JsLookupResult.Undefined.InvalidType -> failure(
                location = lookup.breakpoint,
                error = env.config.errorBuilders.invalidTypeError(expected = lookup.expected, actual = lookup.actual)
            )
        }
    }
