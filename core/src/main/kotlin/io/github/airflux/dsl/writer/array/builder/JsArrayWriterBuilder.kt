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

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.writer.JsArrayWriter
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.writer.array.builder.JsArrayWriterBuilder.WriterBuilder
import io.github.airflux.dsl.writer.array.builder.item.JsArrayItems
import io.github.airflux.dsl.writer.array.builder.item.specification.JsArrayItemSpec

public fun <T> arrayWriter(block: JsArrayWriterBuilder.() -> WriterBuilder<T>): JsArrayWriter<T> =
    JsArrayWriterBuilder().block().build()

@AirfluxMarker
public class JsArrayWriterBuilder internal constructor() {

    public fun interface WriterBuilder<T> {
        public fun build(): JsArrayWriter<T>
    }

    public fun <T : Any> items(spec: JsArrayItemSpec.NonNullable<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(JsArrayItems.NonNullable(spec))
        }

    public fun <T> items(spec: JsArrayItemSpec.Optional<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(JsArrayItems.Optional(spec))
        }

    public fun <T> items(spec: JsArrayItemSpec.Nullable<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(JsArrayItems.Nullable(spec))
        }

    private fun <T> buildArrayWriter(items: JsArrayItems<T>): JsArrayWriter<T> =
        JsArrayWriter { context: JsWriterContext, location: JsLocation, values ->
            JsArray(items = values.mapNotNull { value -> items.write(context, location, value) })
        }
}
