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

package io.github.airflux.serialization.dsl.writer.struct

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.writer.struct.property.StructProperty
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec

public fun <O, CTX, T : Any> structWriter(block: StructWriter.Builder<O, CTX, T>.() -> Unit): Writer<O, CTX, T>
    where O : WriterActionBuilderIfResultIsEmptyOption =
    StructWriter.Builder<O, CTX, T>().apply(block).build()

public class StructWriter<O, CTX, T : Any> private constructor(
    private val properties: List<StructProperty<O, CTX, T>>
) : Writer<O, CTX, T>
    where O : WriterActionBuilderIfResultIsEmptyOption {

    override fun write(env: WriterEnv<O>, context: CTX, location: Location, source: T): ValueNode? {
        val items: Map<String, ValueNode> = mutableMapOf<String, ValueNode>()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(env, context, currentLocation, source)
                        ?.let { value -> this[property.name] = value }
                }
            }
        return if (items.isNotEmpty())
            StructNode(items)
        else
            when (env.options.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> StructNode()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }

    @AirfluxMarker
    public class Builder<O, CTX, T : Any>
        where O : WriterActionBuilderIfResultIsEmptyOption {

        private val properties = mutableListOf<StructProperty<O, CTX, T>>()

        public fun <P : Any> property(
            spec: StructPropertySpec.NonNullable<O, CTX, T, P>
        ): StructProperty.NonNullable<O, CTX, T, P> =
            StructProperty.NonNullable(spec)
                .also { properties.add(it) }

        public fun <P : Any> property(
            spec: StructPropertySpec.Nullable<O, CTX, T, P>
        ): StructProperty.Nullable<O, CTX, T, P> =
            StructProperty.Nullable(spec)
                .also { properties.add(it) }

        public fun build(): Writer<O, CTX, T> = StructWriter(properties)
    }
}
