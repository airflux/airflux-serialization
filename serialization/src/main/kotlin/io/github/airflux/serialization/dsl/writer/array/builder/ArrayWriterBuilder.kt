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
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.array.builder.ArrayWriterBuilder.WriterBuilder
import io.github.airflux.serialization.dsl.writer.array.builder.item.ArrayItemWriter
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.ArrayItemSpec

public fun <CTX, T> arrayWriter(block: ArrayWriterBuilder<CTX>.() -> WriterBuilder<CTX, T>): Writer<CTX, Iterable<T>>
    where CTX : WriterActionBuilderIfResultIsEmptyOption =
    ArrayWriterBuilder<CTX>().block().build()

@AirfluxMarker
public class ArrayWriterBuilder<CTX> internal constructor()
    where CTX : WriterActionBuilderIfResultIsEmptyOption {

    public fun interface WriterBuilder<CTX, T> {
        public fun build(): Writer<CTX, Iterable<T>>
    }

    public fun <T : Any> items(spec: ArrayItemSpec.NonNullable<CTX, T>): WriterBuilder<CTX, T> =
        WriterBuilder {
            buildArrayWriter(ArrayItemWriter.NonNullable(spec))
        }

    public fun <T> items(spec: ArrayItemSpec.Optional<CTX, T>): WriterBuilder<CTX, T> =
        WriterBuilder {
            buildArrayWriter(ArrayItemWriter.Optional(spec))
        }

    public fun <T> items(spec: ArrayItemSpec.Nullable<CTX, T>): WriterBuilder<CTX, T> =
        WriterBuilder {
            buildArrayWriter(ArrayItemWriter.Nullable(spec))
        }
}

internal fun <CTX, T> buildArrayWriter(itemsWriter: ArrayItemWriter<CTX, T>): Writer<CTX, Iterable<T>>
    where CTX : WriterActionBuilderIfResultIsEmptyOption =
    Writer { env: WriterEnv<CTX>, location: Location, values ->
        val result = values.mapNotNull { value -> itemsWriter.write(env, location, value) }
        if (result.isNotEmpty())
            ArrayNode(result)
        else
            when (env.context.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> ArrayNode<Nothing>()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }
