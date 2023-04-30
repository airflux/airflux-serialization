/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure

/**
 * Reads required property.
 *
 * - If a node is found ([lookup] is [JsLookup.Defined]) then applies [reader]
 * - If a node is not found ([lookup] is [JsLookup.Undefined]) then an error is returned
 *   that was build using [PathMissingErrorBuilder]
 */
public fun <EB, O, CTX, T> readRequired(
    env: ReaderEnv<EB, O>,
    context: CTX,
    lookup: JsLookup,
    using: JsReader<EB, O, CTX, T>
): ReadingResult<T>
    where EB : PathMissingErrorBuilder,
          EB : InvalidTypeErrorBuilder =
    when (lookup) {
        is JsLookup.Defined -> using.read(env, context, lookup.location, lookup.value)

        is JsLookup.Undefined -> when (lookup) {
            is JsLookup.Undefined.PathMissing ->
                failure(location = lookup.location, error = env.errorBuilders.pathMissingError())

            is JsLookup.Undefined.InvalidType -> failure(
                location = lookup.breakpoint,
                error = env.errorBuilders.invalidTypeError(expected = lookup.expected, actual = lookup.actual)
            )
        }
    }
