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
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.writer.struct.builder.property.ObjectWriterPropertiesBuilder
import io.github.airflux.serialization.dsl.writer.struct.builder.property.ObjectWriterPropertiesBuilderInstance

public fun <CTX, T : Any> structWriter(block: ObjectWriterBuilder<CTX, T>.() -> Unit): Writer<CTX, T>
    where CTX : WriterActionBuilderIfResultIsEmptyOption =
    ObjectWriterBuilder<CTX, T>(ObjectWriterPropertiesBuilderInstance()).apply(block).build()

@AirfluxMarker
public class ObjectWriterBuilder<CTX, T : Any> internal constructor(
    private val propertiesBuilder: ObjectWriterPropertiesBuilderInstance<CTX, T>
) : ObjectWriterPropertiesBuilder<CTX, T> by propertiesBuilder
    where CTX : WriterActionBuilderIfResultIsEmptyOption {

    internal fun build(): Writer<CTX, T> {
        val properties: ObjectProperties<CTX, T> = propertiesBuilder.build()
        return buildObjectWriter(properties)
    }
}

internal fun <CTX, T : Any> buildObjectWriter(properties: ObjectProperties<CTX, T>): Writer<CTX, T>
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
            ObjectNode(items)
        else
            when (env.context.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> ObjectNode()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }
