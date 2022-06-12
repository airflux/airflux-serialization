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

package io.github.airflux.core.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.failure
import io.github.airflux.core.reader.result.success
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.value.extension.readAsNumber

/**
 * Reader for primitive [Short] type.
 */
public object ShortReader : JsReader<Short> {
    override fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<Short> =
        input.readAsNumber(context, location) { c, l, text ->
            try {
                text.toShort().success(location = l)
            } catch (expected: NumberFormatException) {
                val errorBuilder = c.getValue(ValueCastErrorBuilder)
                errorBuilder.build(text, Short::class).failure(location = l)
            }
        }
}
