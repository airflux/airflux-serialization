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

package io.github.airflux.serialization.dsl.writer.struct.builder.property

import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.StructPropertySpec

public interface StructWriterPropertiesBuilder<CTX, T : Any> {
    public fun <P : Any> property(
        spec: StructPropertySpec.NonNullable<CTX, T, P>
    ): StructProperty.NonNullable<CTX, T, P>

    public fun <P : Any> property(spec: StructPropertySpec.Optional<CTX, T, P>): StructProperty.Optional<CTX, T, P>
    public fun <P : Any> property(spec: StructPropertySpec.Nullable<CTX, T, P>): StructProperty.Nullable<CTX, T, P>
}

internal class StructWriterPropertiesBuilderInstance<CTX, T : Any> : StructWriterPropertiesBuilder<CTX, T> {
    private val properties = mutableListOf<StructProperty<CTX, T>>()

    override fun <P : Any> property(
        spec: StructPropertySpec.NonNullable<CTX, T, P>
    ): StructProperty.NonNullable<CTX, T, P> =
        StructProperty.NonNullable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: StructPropertySpec.Optional<CTX, T, P>): StructProperty.Optional<CTX, T, P> =
        StructProperty.Optional(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: StructPropertySpec.Nullable<CTX, T, P>): StructProperty.Nullable<CTX, T, P> =
        StructProperty.Nullable(spec)
            .also { properties.add(it) }

    internal fun build(): StructProperties<CTX, T> = StructProperties(properties)
}
