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

package io.github.airflux.serialization.dsl.writer.struct.builder

import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.struct.builder.property.StructProperties
import io.github.airflux.serialization.dsl.writer.struct.builder.property.StructWriterPropertiesBuilder
import io.github.airflux.serialization.dsl.writer.struct.builder.property.StructWriterPropertiesBuilderInstance

public fun <CTX, T : Any> structWriter(block: StructWriterBuilder<CTX, T>.() -> Unit): Writer<CTX, T>
    where CTX : WriterActionBuilderIfResultIsEmptyOption =
    StructWriterBuilder<CTX, T>(StructWriterPropertiesBuilderInstance()).apply(block).build()

@AirfluxMarker
public class StructWriterBuilder<CTX, T : Any> internal constructor(
    private val propertiesBuilder: StructWriterPropertiesBuilderInstance<CTX, T>
) : StructWriterPropertiesBuilder<CTX, T> by propertiesBuilder
    where CTX : WriterActionBuilderIfResultIsEmptyOption {

    internal fun build(): Writer<CTX, T> {
        val properties: StructProperties<CTX, T> = propertiesBuilder.build()
        return buildStructWriter(properties)
    }
}

internal fun <CTX, T : Any> buildStructWriter(properties: StructProperties<CTX, T>): Writer<CTX, T>
    where CTX : WriterActionBuilderIfResultIsEmptyOption =
    Writer { env, location, value ->
        val items: Map<String, ValueNode> = mutableMapOf<String, ValueNode>()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(env, currentLocation, value)
                        ?.let { value -> this[property.name] = value }
                }
            }
        if (items.isNotEmpty())
            StructNode(items)
        else
            when (env.context.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> StructNode()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }
