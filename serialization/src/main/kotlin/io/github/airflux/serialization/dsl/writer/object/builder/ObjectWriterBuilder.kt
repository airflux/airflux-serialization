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

package io.github.airflux.serialization.dsl.writer.`object`.builder

import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.ObjectWriter
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.WriterActionBuilderIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.WriterActionConfigurator
import io.github.airflux.serialization.dsl.writer.WriterActionConfiguratorInstance
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.config.JsObjectWriterConfig
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.ObjectWriterPropertiesBuilder
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.ObjectWriterPropertiesBuilderInstance

public fun <T : Any> writer(
    config: JsObjectWriterConfig = JsObjectWriterConfig.DEFAULT,
    block: ObjectWriterBuilder<T>.() -> Unit
): ObjectWriter<T> =
    ObjectWriterBuilder<T>(
        ObjectWriterPropertiesBuilderInstance(),
        WriterActionConfiguratorInstance(config.options.actionIfEmpty)
    ).apply(block).build()

@AirfluxMarker
public class ObjectWriterBuilder<T : Any> internal constructor(
    private val propertiesBuilder: ObjectWriterPropertiesBuilderInstance<T>,
    private val actionConfigurator: WriterActionConfiguratorInstance
) : ObjectWriterPropertiesBuilder<T> by propertiesBuilder,
    WriterActionConfigurator by actionConfigurator {

    internal fun build(): ObjectWriter<T> {
        val properties: ObjectProperties<T> = propertiesBuilder.build()
        return buildObjectWriter(actionIfEmpty, properties)
    }
}

internal fun <T : Any> buildObjectWriter(
    actionIfEmpty: WriterActionBuilderIfResultIsEmpty,
    properties: ObjectProperties<T>
): ObjectWriter<T> =
    ObjectWriter { context, location, input ->
        val items: Map<String, ValueNode> = mutableMapOf<String, ValueNode>()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(context, currentLocation, input)
                        ?.let { value -> this[property.name] = value }
                }
            }
        if (items.isNotEmpty())
            StructNode(items)
        else
            when (actionIfEmpty(context, location)) {
                RETURN_EMPTY_VALUE -> StructNode()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }
