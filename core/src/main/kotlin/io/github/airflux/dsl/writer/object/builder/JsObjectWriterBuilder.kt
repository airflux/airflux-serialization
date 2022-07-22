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

import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsObjectWriter
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectWriterPropertiesBuilder
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectWriterPropertiesBuilderInstance

public fun <T : Any> writer(block: JsObjectWriterBuilder<T>.() -> Unit): JsObjectWriter<T> =
    JsObjectWriterBuilder<T>(JsObjectWriterPropertiesBuilderInstance())
        .apply(block).build()

@AirfluxMarker
public class JsObjectWriterBuilder<T : Any> internal constructor(
    private val propertiesBuilder: JsObjectWriterPropertiesBuilderInstance<T>
) : JsObjectWriterPropertiesBuilder<T> by propertiesBuilder {

    internal fun build(): JsObjectWriter<T> {
        val properties: JsObjectProperties<T> = propertiesBuilder.build()
        return buildObjectWriter(properties)
    }
}

internal fun <T : Any> buildObjectWriter(properties: JsObjectProperties<T>): JsObjectWriter<T> =
    JsObjectWriter { context, location, input ->
        val items: Map<String, JsValue> = mutableMapOf<String, JsValue>()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(context, currentLocation, input)
                        ?.let { value -> this[property.name] = value }
                }
            }
        JsObject(items)
    }
