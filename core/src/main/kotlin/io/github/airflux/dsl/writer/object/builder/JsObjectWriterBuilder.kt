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
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty.Action
import io.github.airflux.core.writer.context.option.writeActionIfObjectIsEmpty
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.writer.`object`.builder.property.JsObjectProperty
import io.github.airflux.dsl.writer.`object`.builder.property.specification.JsObjectPropertySpec

@AirfluxMarker
public class JsObjectWriterBuilder<T : Any> internal constructor() {

    private val propertiesBuilder = JsObjectProperties.Builder<T>()

    public fun <P : Any> property(spec: JsObjectPropertySpec.Required<T, P>): JsObjectProperty.Required<T, P> =
        JsObjectProperty.Required(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.Optional<T, P>): JsObjectProperty.Optional<T, P> =
        JsObjectProperty.Optional(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.Nullable<T, P>): JsObjectProperty.Nullable<T, P> =
        JsObjectProperty.Nullable(spec)
            .also { propertiesBuilder.add(it) }

    internal fun build(): JsObjectWriter<T> {
        val properties = propertiesBuilder.build()
        return JsObjectWriter { context, location, input ->
            val items = properties.mapNotNull { property ->
                val currentLocation = location.append(property.name)
                property.write(context, currentLocation, input)
                    ?.let { value -> property.name to value }
            }

            if (items.isNotEmpty())
                JsObject(items.toMap())
            else
                valueIfObjectIsEmpty(context)
        }
    }

    internal companion object {

        internal fun valueIfObjectIsEmpty(context: JsWriterContext): JsValue? =
            when (context.writeActionIfObjectIsEmpty) {
                Action.EMPTY -> JsObject()
                Action.NULL -> JsNull
                Action.SKIP -> null
            }
    }
}
