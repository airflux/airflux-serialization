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

package io.github.airflux.serialization.dsl.reader.array.builder.item.specification

import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.JsValidator

public fun <T : Any> nonNullable(reader: JsReader<T>): JsArrayItemSpec.NonNullable<T> =
    JsArrayItemSpec.NonNullable(reader)

public infix fun <T> JsArrayItemSpec.NonNullable<T>.validation(
    validator: JsValidator<T>
): JsArrayItemSpec.NonNullable<T> =
    JsArrayItemSpec.NonNullable(
        reader = { context, location, input ->
            reader.read(context, location, input).validation(context, validator)
        }
    )

public infix fun <T> JsArrayItemSpec.NonNullable<T>.or(
    alt: JsArrayItemSpec.NonNullable<T>
): JsArrayItemSpec.NonNullable<T> =
    JsArrayItemSpec.NonNullable(reader = reader or alt.reader)
