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

package io.github.airflux.dsl.writer.`object`.builder

import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsObjectWriter
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.config.JsObjectWriterConfig
import io.github.airflux.dsl.writer.WriterActionBuilderIfResultIsEmpty
import io.github.airflux.dsl.writer.WriterActionConfigurator
import io.github.airflux.dsl.writer.WriterActionConfiguratorInstance
import io.github.airflux.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectWriterPropertiesBuilder
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectWriterPropertiesBuilderInstance

public fun <T : Any> writer(
    config: JsObjectWriterConfig = JsObjectWriterConfig.DEFAULT,
    block: JsObjectWriterBuilder<T>.() -> Unit
): JsObjectWriter<T> =
    JsObjectWriterBuilder<T>(
        JsObjectWriterPropertiesBuilderInstance(),
        WriterActionConfiguratorInstance(config.options.actionIfEmpty)
    ).apply(block).build()

@AirfluxMarker
public class JsObjectWriterBuilder<T : Any> internal constructor(
    private val propertiesBuilder: JsObjectWriterPropertiesBuilderInstance<T>,
    private val actionConfigurator: WriterActionConfiguratorInstance
) : JsObjectWriterPropertiesBuilder<T> by propertiesBuilder,
    WriterActionConfigurator by actionConfigurator {

    internal fun build(): JsObjectWriter<T> {
        val properties: JsObjectProperties<T> = propertiesBuilder.build()
        return buildObjectWriter(actionIfEmpty, properties)
    }
}

internal fun <T : Any> buildObjectWriter(
    actionIfEmpty: WriterActionBuilderIfResultIsEmpty,
    properties: JsObjectProperties<T>
): JsObjectWriter<T> =
    JsObjectWriter { context, location, input ->
        val items: Map<String, JsValue> = mutableMapOf<String, JsValue>()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(context, currentLocation, input)
                        ?.let { value -> this[property.name] = value }
                }
            }
        if (items.isNotEmpty())
            JsObject(items)
        else
            when (actionIfEmpty(context, location)) {
                RETURN_EMPTY_VALUE -> JsObject()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> JsNull
            }
    }
