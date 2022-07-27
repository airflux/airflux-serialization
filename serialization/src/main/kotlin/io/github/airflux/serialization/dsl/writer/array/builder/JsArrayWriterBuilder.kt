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

package io.github.airflux.serialization.dsl.writer.array.builder

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.writer.JsArrayWriter
import io.github.airflux.serialization.core.writer.context.JsWriterContext
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionBuilderIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.WriterActionConfigurator
import io.github.airflux.serialization.dsl.writer.WriterActionConfiguratorInstance
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.array.builder.JsArrayWriterBuilder.WriterBuilder
import io.github.airflux.serialization.dsl.writer.array.builder.item.JsArrayItems
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.JsArrayItemSpec
import io.github.airflux.serialization.dsl.writer.config.JsArrayWriterConfig

public fun <T> arrayWriter(
    config: JsArrayWriterConfig = JsArrayWriterConfig.DEFAULT,
    block: JsArrayWriterBuilder.() -> WriterBuilder<T>
): JsArrayWriter<T> =
    JsArrayWriterBuilder(WriterActionConfiguratorInstance(config.options.actionIfEmpty)).block().build()

@AirfluxMarker
public class JsArrayWriterBuilder internal constructor(
    private val actionConfigurator: WriterActionConfiguratorInstance
) : WriterActionConfigurator by actionConfigurator {

    public fun interface WriterBuilder<T> {
        public fun build(): JsArrayWriter<T>
    }

    public fun <T : Any> items(spec: JsArrayItemSpec.NonNullable<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(actionIfEmpty, JsArrayItems.NonNullable(spec))
        }

    public fun <T> items(spec: JsArrayItemSpec.Optional<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(actionIfEmpty, JsArrayItems.Optional(spec))
        }

    public fun <T> items(spec: JsArrayItemSpec.Nullable<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(actionIfEmpty, JsArrayItems.Nullable(spec))
        }
}

internal fun <T> buildArrayWriter(
    actionIfEmpty: WriterActionBuilderIfResultIsEmpty,
    items: JsArrayItems<T>
): JsArrayWriter<T> =
    JsArrayWriter { context: JsWriterContext, location: JsLocation, values ->
        val result = values.mapNotNull { value -> items.write(context, location, value) }

        if (result.isNotEmpty())
            JsArray(result)
        else
            when (actionIfEmpty(context, location)) {
                RETURN_EMPTY_VALUE -> JsArray<Nothing>()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> JsNull
            }
    }
