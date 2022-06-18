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

package io.github.airflux.dsl.writer.array.builder

import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsArrayWriter
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.WriteActionIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.writeActionIfArrayIsEmpty
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.writer.array.builder.item.specification.JsArrayItemSpec

@AirfluxMarker
public class JsArrayWriterBuilder<T> internal constructor() {

    public class WriterBuilder<T> internal constructor(internal val build: () -> JsArrayWriter<T>)

    public fun returns(items: JsArrayItemSpec<T>): WriterBuilder<T> {
        val writer = items.writer
        return WriterBuilder(
            build = {
                JsArrayWriter { context, location, value ->
                    val values = value.mapNotNull { item -> writer.write(context, location, item) }
                    if (values.isNotEmpty())
                        JsArray(items = values)
                    else
                        valueIfArrayIsEmpty(context)
                }
            }
        )
    }

    internal companion object {
        internal fun valueIfArrayIsEmpty(context: JsWriterContext): JsValue? =
            when (context.writeActionIfArrayIsEmpty) {
                WriteActionIfArrayIsEmpty.Action.EMPTY -> JsObject()
                WriteActionIfArrayIsEmpty.Action.NULL -> JsNull
                WriteActionIfArrayIsEmpty.Action.SKIP -> null
            }
    }
}
