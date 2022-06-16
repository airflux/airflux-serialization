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

package io.github.airflux.core.writer.`object`

import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsArrayWriter
import io.github.airflux.core.writer.JsObjectWriter
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.`object`.builder.JsWriterProperty
import io.github.airflux.core.writer.`object`.builder.JsWriterPropertyBuilder
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.writer.config.ObjectWriterConfig

public fun <T : Any> T.serialization(writer: JsWriter<T>): JsValue = writer.write(this)

public class ObjectWriter(private val configuration: ObjectWriterConfig) {

    public operator fun <T : Any> invoke(init: Builder<T>.() -> Unit): JsObjectWriter<T> =
        Builder<T>().apply(init).build(configuration)

    @AirfluxMarker
    public class Builder<T : Any> internal constructor() {
        private val properties = mutableListOf<JsWriterPropertyBuilder<T>>()

        public fun <P : Any> requiredProperty(name: String, from: (T) -> P, writer: JsWriter<P>) {
            JsWriterPropertyBuilder.Required(name, from, writer).also { properties.add(it) }
        }

        public fun <P : Any> optionalProperty(
            name: String,
            from: (T) -> P?,
            writer: JsWriter<P>
        ): JsWriterProperty.Optional.Simple<T, P> =
            JsWriterPropertyBuilder.Optional.Simple(name, from, writer).also { properties.add(it) }

        public fun <P : Any> optionalProperty(
            name: String,
            from: (T) -> P?,
            writer: JsArrayWriter<P>
        ): JsWriterProperty.Optional.Array<T, P> =
            JsWriterPropertyBuilder.Optional.Array(name, from, writer).also { properties.add(it) }

        public fun <P : Any> optionalProperty(
            name: String,
            from: (T) -> P?,
            writer: JsObjectWriter<P>
        ): JsWriterProperty.Optional.Object<T, P> =
            JsWriterPropertyBuilder.Optional.Object(name, from, writer).also { properties.add(it) }

        public fun <P : Any> nullableProperty(
            name: String,
            from: (T) -> P?,
            writer: JsWriter<P>
        ): JsWriterProperty.Nullable.Simple<T, P> =
            JsWriterPropertyBuilder.Nullable.Simple(name, from, writer).also { properties.add(it) }

        public fun <P : Any> nullableProperty(
            name: String,
            from: (T) -> P?,
            writer: JsArrayWriter<P>
        ): JsWriterProperty.Nullable.Array<T, P> =
            JsWriterPropertyBuilder.Nullable.Array(name, from, writer).also { properties.add(it) }

        public fun <P : Any> nullableProperty(
            name: String,
            from: (T) -> P?,
            writer: JsObjectWriter<P>
        ): JsWriterProperty.Nullable.Object<T, P> =
            JsWriterPropertyBuilder.Nullable.Object(name, from, writer).also { properties.add(it) }

        internal fun build(configuration: ObjectWriterConfig): JsObjectWriter<T> {
            val propertiesByName = properties.asSequence()
                .map { property -> property.name to property.buildConverter(configuration) }
                .toMap()

            return JsObjectWriter { value ->
                mutableMapOf<String, JsValue>()
                    .apply {
                        propertiesByName.forEach { (name, converter) ->
                            converter(value)
                                ?.also { this[name] = it }
                        }
                    }
                    .let { JsObject(it) }
            }
        }
    }
}
