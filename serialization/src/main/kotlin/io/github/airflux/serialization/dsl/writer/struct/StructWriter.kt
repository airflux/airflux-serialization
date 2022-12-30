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
import io.github.airflux.serialization.dsl.writer.struct.property.StructProperties
import io.github.airflux.serialization.dsl.writer.struct.property.StructProperty
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec

public fun <CTX, T : Any> structWriter(block: StructWriter.Builder<CTX, T>.() -> Unit): Writer<CTX, T>
    where CTX : WriterActionBuilderIfResultIsEmptyOption =
    StructWriter.Builder<CTX, T>().apply(block).build()

public class StructWriter<CTX, T : Any> private constructor(
    private val properties: StructProperties<CTX, T>
) : Writer<CTX, T>
    where CTX : WriterActionBuilderIfResultIsEmptyOption {

    override fun write(env: WriterEnv<CTX>, location: Location, source: T): ValueNode? {
        val items: Map<String, ValueNode> = mutableMapOf<String, ValueNode>()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(env, currentLocation, source)
                        ?.let { value -> this[property.name] = value }
                }
            }
        return if (items.isNotEmpty())
            StructNode(items)
        else
            when (env.context.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> StructNode()
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> NullNode
            }
    }

    @AirfluxMarker
    public class Builder<CTX, T : Any>
        where CTX : WriterActionBuilderIfResultIsEmptyOption {

        private val properties = mutableListOf<StructProperty<CTX, T>>()

        public fun <P : Any> property(
            spec: StructPropertySpec.NonNullable<CTX, T, P>
        ): StructProperty.NonNullable<CTX, T, P> =
            StructProperty.NonNullable(spec)
                .also { properties.add(it) }

        public fun <P : Any> property(
            spec: StructPropertySpec.Nullable<CTX, T, P>
        ): StructProperty.Nullable<CTX, T, P> =
            StructProperty.Nullable(spec)
                .also { properties.add(it) }

        public fun build(): Writer<CTX, T> = StructWriter(StructProperties(properties))
    }
}
