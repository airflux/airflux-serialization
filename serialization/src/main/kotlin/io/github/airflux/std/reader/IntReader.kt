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

package io.github.airflux.std.reader

import io.github.airflux.core.context.error.get
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.failure
import io.github.airflux.core.reader.result.success
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.value.readAsNumber

/**
 * Reader for primitive [Int] type.
 */
public object IntReader : JsReader<Int> {
    override fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<Int> =
        input.readAsNumber(context, location) { ctx, l, text ->
            try {
                text.toInt().success(location = l)
            } catch (expected: NumberFormatException) {
                val errorBuilder = ctx[ValueCastErrorBuilder]
                errorBuilder.build(text, Int::class).failure(location = l)
            }
        }
}
