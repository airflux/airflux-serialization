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

package io.github.airflux.serialization.dsl.reader.struct.builder.property

import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.StructPropertySpec

public interface StructReaderPropertiesBuilder<EB, CTX> {

    public fun <P : Any> property(spec: StructPropertySpec.Required<EB, CTX, P>): StructProperty.Required<EB, CTX, P>

    public fun <P : Any> property(
        spec: StructPropertySpec.RequiredIf<EB, CTX, P>
    ): StructProperty.RequiredIf<EB, CTX, P>

    public fun <P : Any> property(
        spec: StructPropertySpec.Defaultable<EB, CTX, P>
    ): StructProperty.Defaultable<EB, CTX, P>

    public fun <P : Any> property(spec: StructPropertySpec.Optional<EB, CTX, P>): StructProperty.Optional<EB, CTX, P>

    public fun <P : Any> property(
        spec: StructPropertySpec.OptionalWithDefault<EB, CTX, P>
    ): StructProperty.OptionalWithDefault<EB, CTX, P>

    public fun <P : Any> property(spec: StructPropertySpec.Nullable<EB, CTX, P>): StructProperty.Nullable<EB, CTX, P>

    public fun <P : Any> property(
        spec: StructPropertySpec.NullableWithDefault<EB, CTX, P>
    ): StructProperty.NullableWithDefault<EB, CTX, P>
}

internal class StructReaderPropertiesBuilderInstance<EB, CTX> : StructReaderPropertiesBuilder<EB, CTX> {
    private val properties = mutableListOf<StructProperty<EB, CTX>>()

    override fun <P : Any> property(
        spec: StructPropertySpec.Required<EB, CTX, P>
    ): StructProperty.Required<EB, CTX, P> =
        StructProperty.Required(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: StructPropertySpec.RequiredIf<EB, CTX, P>
    ): StructProperty.RequiredIf<EB, CTX, P> =
        StructProperty.RequiredIf(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: StructPropertySpec.Defaultable<EB, CTX, P>
    ): StructProperty.Defaultable<EB, CTX, P> =
        StructProperty.Defaultable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: StructPropertySpec.Optional<EB, CTX, P>
    ): StructProperty.Optional<EB, CTX, P> =
        StructProperty.Optional(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: StructPropertySpec.OptionalWithDefault<EB, CTX, P>
    ): StructProperty.OptionalWithDefault<EB, CTX, P> =
        StructProperty.OptionalWithDefault(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: StructPropertySpec.Nullable<EB, CTX, P>
    ): StructProperty.Nullable<EB, CTX, P> =
        StructProperty.Nullable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: StructPropertySpec.NullableWithDefault<EB, CTX, P>
    ): StructProperty.NullableWithDefault<EB, CTX, P> =
        StructProperty.NullableWithDefault(spec)
            .also { properties.add(it) }

    fun build(): StructProperties<EB, CTX> = StructProperties(properties)
}
