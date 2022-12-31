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
import io.github.airflux.serialization.core.reader.result.ReaderResult

/**
 * Reads optional property.
 *
 * - If a node is found ([lookup] is [LookupResult.Defined]) then applies [reader]
 * - If a node is not found ([lookup] is [LookupResult.Undefined]) then returns 'null'
 */
public fun <EB, CTX, T : Any> readOptional(
    env: ReaderEnv<EB, CTX>,
    lookup: LookupResult,
    using: Reader<EB, CTX, T>
): ReaderResult<T?> =
    when (lookup) {
        is LookupResult.Defined -> using.read(env, lookup.location, lookup.value)
        is LookupResult.Undefined -> ReaderResult.Success(location = lookup.location, value = null)
    }
