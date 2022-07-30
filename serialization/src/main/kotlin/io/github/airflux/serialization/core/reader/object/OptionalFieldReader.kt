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

package io.github.airflux.serialization.core.reader.`object`

import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult

/**
 * Reads optional field.
 *
 * - If a node is found ([from] is [Lookup.Defined]) then applies [reader]
 * - If a node is not found ([from] is [Lookup.Undefined]) then returns 'null'
 */
public fun <T : Any> readOptional(context: ReaderContext, from: Lookup, using: Reader<T>): ReaderResult<T?> =
    when (from) {
        is Lookup.Defined -> using.read(context, from.location, from.value)
        is Lookup.Undefined -> ReaderResult.Success(location = from.location, value = null)
    }
