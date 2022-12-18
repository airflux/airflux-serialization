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

import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.NullNode

/**
 * Reads required property or return default if a property is not found.
 *
 * - If a node is found with a value no 'null' ([lookup] is [Lookup.Defined]) then applies [reader]
 * - If a node is found with a value 'null' ([lookup] is [Lookup.Defined]) then returns [defaultValue]
 * - If a node is not found ([lookup] is [Lookup.Undefined]) then returns [defaultValue]
 */
public fun <EB, CTX, T : Any> readWithDefault(
    env: ReaderEnv<EB, CTX>,
    lookup: Lookup,
    using: Reader<EB, CTX, T>,
    defaultValue: (ReaderEnv<EB, CTX>) -> T
): ReaderResult<T> {

    fun <EB, CTX, T : Any> readWithDefault(
        env: ReaderEnv<EB, CTX>,
        lookup: Lookup.Defined,
        using: Reader<EB, CTX, T>,
        defaultValue: (ReaderEnv<EB, CTX>) -> T
    ): ReaderResult<T> =
        if (lookup.value is NullNode)
            ReaderResult.Success(location = lookup.location, value = defaultValue(env))
        else
            using.read(env, lookup.location, lookup.value)

    return when (lookup) {
        is Lookup.Defined -> readWithDefault(env, lookup, using, defaultValue)
        is Lookup.Undefined -> ReaderResult.Success(location = lookup.location, value = defaultValue(env))
    }
}
