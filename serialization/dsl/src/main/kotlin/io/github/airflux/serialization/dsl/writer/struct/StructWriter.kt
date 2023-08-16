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

package io.github.airflux.serialization.dsl.writer.struct

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.struct.property.StructProperties
import io.github.airflux.serialization.dsl.writer.struct.property.StructProperty
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec

public fun <O, T> structWriter(block: StructWriter.Builder<O, T>.() -> Unit): JsWriter<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption =
    StructWriter.Builder<O, T>().apply(block).build()

public class StructWriter<O, T> private constructor(
    private val properties: StructProperties<O, T>
) : JsWriter<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption {

    override fun write(env: JsWriterEnv<O>, context: JsContext, location: JsLocation, source: T): JsValue? {
        val struct = JsStruct.builder()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(env, context, currentLocation, source)
                        ?.let { value -> add(name = property.name, value = value) }
                }
            }
            .build()

        return if (struct.isEmpty())
            when (env.options.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> struct
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> JsNull
            }
        else
            struct
    }

    @AirfluxMarker
    public class Builder<O, T> internal constructor()
        where O : WriterActionBuilderIfResultIsEmptyOption {

        private val properties = mutableListOf<StructProperty<O, T, *>>()

        public fun <P> property(spec: StructPropertySpec<O, T, P>): StructProperty<O, T, P> =
            StructProperty(spec).also { properties.add(it) }

        internal fun build(): JsWriter<O, T> = StructWriter(properties)
    }
}
