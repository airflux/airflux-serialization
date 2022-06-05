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

@file:Suppress("unused", "TooManyFunctions")

package io.github.airflux.dsl.reader.array.item.specification

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull

public fun <T : Any> nonNullable(reader: JsReader<T>): JsArrayItemSpec.NonNullable<T> =
    JsArrayNonNullableItemSpec(reader)

public fun <T : Any> nullable(reader: JsReader<T>): JsArrayItemSpec.Nullable<T?> = JsArrayNullableItemSpec(
    reader = { context, location, input ->
        if (input is JsNull)
            JsResult.Success(location = location, value = null)
        else
            reader.read(context, location, input)
    }
)
