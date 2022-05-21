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
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.asFailure
import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.core.value.extension.readAsNumber

/**
 * Reader for primitive [Long] type.
 */
fun buildLongReader(): JsReader<Long> =
    JsReader { context, location, input ->
        input.readAsNumber(context, location) { c, p, text ->
            try {
                text.toLong().asSuccess(location = p)
            } catch (expected: NumberFormatException) {
                val errorBuilder = c.getValue(ValueCastErrorBuilder)
                errorBuilder.build(text, Long::class).asFailure(location = p)
            }
        }
    }
