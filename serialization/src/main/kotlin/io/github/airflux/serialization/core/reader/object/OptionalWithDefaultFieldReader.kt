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

import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult

/**
 * Reads optional field or return default if a field is not found.
 *
 * - If a node is found ([from] is [JsLookup.Defined]) then applies [reader]
 * - If a node is not found ([from] is [JsLookup.Undefined]) then returns [defaultValue]
 */
public fun <T : Any> readOptional(
    context: JsReaderContext,
    from: JsLookup,
    using: JsReader<T>,
    defaultValue: () -> T
): JsResult<T> =
    when (from) {
        is JsLookup.Defined -> using.read(context, from.location, from.value)
        is JsLookup.Undefined -> JsResult.Success(location = from.location, value = defaultValue())
    }
