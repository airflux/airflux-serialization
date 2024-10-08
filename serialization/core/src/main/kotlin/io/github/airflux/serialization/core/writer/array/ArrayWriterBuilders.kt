/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.writer.array

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE

public fun <O, T> buildArrayWriter(itemsWriter: JsWriter<O, T>): JsArrayWriter<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption =
    JsArrayWriter { env: JsWriterEnv<O>, location: JsLocation, source: Iterable<T> ->
        val result = source.mapNotNull { item -> itemsWriter.write(env, location, item) }
        if (result.isNotEmpty())
            JsArray(result)
        else
            when (env.config.options.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> JsArray()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> JsNull
            }
    }
