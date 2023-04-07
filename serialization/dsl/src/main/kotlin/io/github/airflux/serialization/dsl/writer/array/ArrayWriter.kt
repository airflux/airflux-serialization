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
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE

public fun <O, CTX, T> arrayWriter(items: Writer<O, CTX, T>): Writer<O, CTX, Iterable<T>>
    where O : WriterActionBuilderIfResultIsEmptyOption =
    ArrayWriter(items)

public class ArrayWriter<O, CTX, T> internal constructor(
    private val itemsWriter: Writer<O, CTX, T>
) : Writer<O, CTX, Iterable<T>>
    where O : WriterActionBuilderIfResultIsEmptyOption {

    override fun write(env: WriterEnv<O>, context: CTX, location: Location, source: Iterable<T>): ValueNode? {
        val result = source.mapNotNull { item -> itemsWriter.write(env, context, location, item) }
        return if (result.isNotEmpty())
            ArrayNode(result)
        else
            when (env.options.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> ArrayNode()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }
}
