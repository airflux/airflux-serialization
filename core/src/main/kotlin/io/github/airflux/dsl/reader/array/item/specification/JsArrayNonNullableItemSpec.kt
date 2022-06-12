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

package io.github.airflux.dsl.reader.array.item.specification

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.or
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.reader.validator.extension.validation

internal class JsArrayNonNullableItemSpec<T>(override val reader: JsReader<T>) : JsArrayItemSpec.NonNullable<T> {

    override infix fun validation(validator: JsValidator<T>): JsArrayItemSpec.NonNullable<T> =
        JsArrayNonNullableItemSpec(
            reader = { context, location, input ->
                reader.read(context, location, input).validation(context, validator)
            }
        )

    override fun or(alt: JsArrayItemSpec.NonNullable<T>): JsArrayItemSpec.NonNullable<T> =
        JsArrayNonNullableItemSpec(reader or alt.reader)
}
