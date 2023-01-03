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

package io.github.airflux.serialization.dsl.writer.array

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.array.item.ArrayItemWriter
import io.github.airflux.serialization.dsl.writer.array.item.specification.ArrayItemSpec

public fun <CTX, T> arrayWriter(
    block: ArrayWriter.Builder<CTX>.() -> Writer<CTX, Iterable<T>>
): Writer<CTX, Iterable<T>>
    where CTX : WriterActionBuilderIfResultIsEmptyOption {
    val builder = ArrayWriter.Builder<CTX>()
    return block(builder)
}

public fun <CTX, T : Any> ArrayWriter.Builder<CTX>.items(
    spec: ArrayItemSpec.NonNullable<CTX, T>
): Writer<CTX, Iterable<T>>
    where CTX : WriterActionBuilderIfResultIsEmptyOption = this.build(spec)

public fun <CTX, T> ArrayWriter.Builder<CTX>.items(
    spec: ArrayItemSpec.Nullable<CTX, T>
): Writer<CTX, Iterable<T>>
    where CTX : WriterActionBuilderIfResultIsEmptyOption = this.build(spec)

public class ArrayWriter<CTX, T> private constructor(
    private val itemsWriter: ArrayItemWriter<CTX, T>
) : Writer<CTX, Iterable<T>>
    where CTX : WriterActionBuilderIfResultIsEmptyOption {

    override fun write(env: WriterEnv<CTX>, location: Location, source: Iterable<T>): ValueNode? {
        val result = source.mapNotNull { item -> itemsWriter.write(env, location, item) }
        return if (result.isNotEmpty())
            ArrayNode(result)
        else
            when (env.context.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> ArrayNode<Nothing>()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }

    @AirfluxMarker
    public class Builder<CTX>
        where CTX : WriterActionBuilderIfResultIsEmptyOption {

        public fun <T : Any> build(spec: ArrayItemSpec.NonNullable<CTX, T>): Writer<CTX, Iterable<T>> =
            ArrayWriter(ArrayItemWriter.NonNullable(spec))

        public fun <T> build(spec: ArrayItemSpec.Nullable<CTX, T>): Writer<CTX, Iterable<T>> =
            ArrayWriter(ArrayItemWriter.Nullable(spec))
    }
}