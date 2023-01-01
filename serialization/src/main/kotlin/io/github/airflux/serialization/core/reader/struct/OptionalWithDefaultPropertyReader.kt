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

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.core.lookup.LookupResult
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult

/**
 * Reads optional property or return default if a property is not found.
 *
 * - If a node is found ([lookup] is [LookupResult.Defined]) then applies [reader]
 * - If a node is not found ([lookup] is [LookupResult.Undefined]) then returns [defaultValue]
 */
public fun <EB, CTX, T : Any> readOptional(
    env: ReaderEnv<EB, CTX>,
    lookup: LookupResult,
    using: Reader<EB, CTX, T>,
    defaultValue: (ReaderEnv<EB, CTX>) -> T
): ReaderResult<T>
    where EB : InvalidTypeErrorBuilder =
    when (lookup) {
        is LookupResult.Defined -> using.read(env, lookup.location, lookup.value)

        is LookupResult.Undefined -> when (lookup) {
            is LookupResult.Undefined.PathMissing ->
                ReaderResult.Success(location = lookup.location, value = defaultValue(env))

            is LookupResult.Undefined.InvalidType -> ReaderResult.Failure(
                location = lookup.location,
                error = env.errorBuilders.invalidTypeError(expected = lookup.expected, actual = lookup.actual)
            )
        }
    }
