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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.writer.ArrayWriter
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionBuilderIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.WriterActionConfigurator
import io.github.airflux.serialization.dsl.writer.WriterActionConfiguratorInstance
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.array.builder.ArrayWriterBuilder.WriterBuilder
import io.github.airflux.serialization.dsl.writer.array.builder.item.ArrayItemWriter
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.ArrayItemSpec
import io.github.airflux.serialization.dsl.writer.config.ArrayWriterConfig

public fun <T> arrayWriter(
    config: ArrayWriterConfig = ArrayWriterConfig.DEFAULT,
    block: ArrayWriterBuilder.() -> WriterBuilder<T>
): ArrayWriter<T> =
    ArrayWriterBuilder(WriterActionConfiguratorInstance(config.options.actionIfEmpty)).block().build()

@AirfluxMarker
public class ArrayWriterBuilder internal constructor(
    private val actionConfigurator: WriterActionConfiguratorInstance
) : WriterActionConfigurator by actionConfigurator {

    public fun interface WriterBuilder<T> {
        public fun build(): ArrayWriter<T>
    }

    public fun <T : Any> items(spec: ArrayItemSpec.NonNullable<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(actionIfEmpty, ArrayItemWriter.NonNullable(spec))
        }

    public fun <T> items(spec: ArrayItemSpec.Optional<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(actionIfEmpty, ArrayItemWriter.Optional(spec))
        }

    public fun <T> items(spec: ArrayItemSpec.Nullable<T>): WriterBuilder<T> =
        WriterBuilder {
            buildArrayWriter(actionIfEmpty, ArrayItemWriter.Nullable(spec))
        }
}

internal fun <T> buildArrayWriter(
    actionIfEmpty: WriterActionBuilderIfResultIsEmpty,
    itemsWriter: ArrayItemWriter<T>
): ArrayWriter<T> =
    ArrayWriter { context: WriterContext, location: Location, values ->
        val result = values.mapNotNull { value -> itemsWriter.write(context, location, value) }

        if (result.isNotEmpty())
            ArrayNode(result)
        else
            when (actionIfEmpty(context, location)) {
                RETURN_EMPTY_VALUE -> ArrayNode<Nothing>()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }
