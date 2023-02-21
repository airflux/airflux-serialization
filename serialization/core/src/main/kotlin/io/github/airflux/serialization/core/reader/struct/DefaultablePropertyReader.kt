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

import io.github.airflux.serialization.core.lookup.LookupResult
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.NullNode

/**
 * Reads required property or return default if a property is not found.
 *
 * - If a node is found with a value no 'null' ([lookup] is [LookupResult.Defined]) then applies [reader]
 * - If a node is found with a value 'null' ([lookup] is [LookupResult.Defined]) then returns [defaultValue]
 * - If a node is not found ([lookup] is [LookupResult.Undefined]) then returns [defaultValue]
 */
public fun <EB, O, CTX, T : Any> readWithDefault(
    env: ReaderEnv<EB, O>,
    context: CTX,
    lookup: LookupResult,
    using: Reader<EB, O, CTX, T>,
    defaultValue: (ReaderEnv<EB, O>, CTX) -> T
): ReaderResult<T>
    where EB : InvalidTypeErrorBuilder {

    fun <EB, O, CTX, T : Any> readWithDefault(
        env: ReaderEnv<EB, O>,
        context: CTX,
        lookup: LookupResult.Defined,
        using: Reader<EB, O, CTX, T>,
        defaultValue: (ReaderEnv<EB, O>, CTX) -> T
    ): ReaderResult<T> =
        if (lookup.value is NullNode)
            ReaderResult.Success(location = lookup.location, value = defaultValue(env, context))
        else
            using.read(env, context, lookup.location, lookup.value)

    return when (lookup) {
        is LookupResult.Defined -> readWithDefault(env, context, lookup, using, defaultValue)

        is LookupResult.Undefined -> when (lookup) {
            is LookupResult.Undefined.PathMissing ->
                ReaderResult.Success(location = lookup.location, value = defaultValue(env, context))

            is LookupResult.Undefined.InvalidType -> ReaderResult.Failure(
                location = lookup.breakpoint,
                error = env.errorBuilders.invalidTypeError(expected = lookup.expected, actual = lookup.actual)
            )
        }
    }
}