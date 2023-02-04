/*
 * Copyright 2021-2023 Maxim Sambulat.
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

public fun <O, CTX, T> arrayWriter(
    block: ArrayWriter.Builder<O, CTX>.() -> Writer<O, CTX, Iterable<T>>
): Writer<O, CTX, Iterable<T>>
    where O : WriterActionBuilderIfResultIsEmptyOption {
    val builder = ArrayWriter.Builder<O, CTX>()
    return block(builder)
}

public fun <O, CTX, T : Any> ArrayWriter.Builder<O, CTX>.items(
    spec: ArrayItemSpec.NonNullable<O, CTX, T>
): Writer<O, CTX, Iterable<T>>
    where O : WriterActionBuilderIfResultIsEmptyOption = this.build(spec)

public fun <O, CTX, T> ArrayWriter.Builder<O, CTX>.items(
    spec: ArrayItemSpec.Nullable<O, CTX, T>
): Writer<O, CTX, Iterable<T>>
    where O : WriterActionBuilderIfResultIsEmptyOption = this.build(spec)

public class ArrayWriter<O, CTX, T> private constructor(
    private val itemsWriter: ArrayItemWriter<O, CTX, T>
) : Writer<O, CTX, Iterable<T>>
    where O : WriterActionBuilderIfResultIsEmptyOption {

    override fun write(env: WriterEnv<O>, context: CTX, location: Location, source: Iterable<T>): ValueNode? {
        val result = source.mapNotNull { item -> itemsWriter.write(env, context, location, item) }
        return if (result.isNotEmpty())
            ArrayNode(result)
        else
            when (env.options.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> ArrayNode<Nothing>()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }

    @AirfluxMarker
    public class Builder<O, CTX>
        where O : WriterActionBuilderIfResultIsEmptyOption {

        public fun <T : Any> build(spec: ArrayItemSpec.NonNullable<O, CTX, T>): Writer<O, CTX, Iterable<T>> =
            ArrayWriter(ArrayItemWriter.NonNullable(spec))

        public fun <T> build(spec: ArrayItemSpec.Nullable<O, CTX, T>): Writer<O, CTX, Iterable<T>> =
            ArrayWriter(ArrayItemWriter.Nullable(spec))
    }
}
