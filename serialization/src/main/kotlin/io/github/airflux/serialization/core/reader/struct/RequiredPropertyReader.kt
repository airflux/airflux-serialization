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

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult

/**
 * Reads required property.
 *
 * - If a node is found ([lookup] is [Lookup.Defined]) then applies [reader]
 * - If a node is not found ([lookup] is [Lookup.Undefined]) then an error is returned
 *   that was build using [PathMissingErrorBuilder]
 */
public fun <T : Any> readRequired(context: ReaderContext, lookup: Lookup, using: Reader<T>): ReaderResult<T> =
    when (lookup) {
        is Lookup.Defined -> using.read(context, lookup.location, lookup.value)
        is Lookup.Undefined -> {
            val errorBuilder = context[PathMissingErrorBuilder]
            ReaderResult.Failure(location = lookup.location, error = errorBuilder.build())
        }
    }
