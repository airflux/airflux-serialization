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

import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.ObjectPropertySpec

public interface ObjectReaderPropertiesBuilder<EB, CTX> {

    public fun <P : Any> property(spec: ObjectPropertySpec.Required<EB, CTX, P>): ObjectProperty.Required<EB, CTX, P>

    public fun <P : Any> property(
        spec: ObjectPropertySpec.Defaultable<EB, CTX, P>
    ): ObjectProperty.Defaultable<EB, CTX, P>

    public fun <P : Any> property(spec: ObjectPropertySpec.Optional<EB, CTX, P>): ObjectProperty.Optional<EB, CTX, P>

    public fun <P : Any> property(
        spec: ObjectPropertySpec.OptionalWithDefault<EB, CTX, P>
    ): ObjectProperty.OptionalWithDefault<EB, CTX, P>

    public fun <P : Any> property(spec: ObjectPropertySpec.Nullable<EB, CTX, P>): ObjectProperty.Nullable<EB, CTX, P>

    public fun <P : Any> property(
        spec: ObjectPropertySpec.NullableWithDefault<EB, CTX, P>
    ): ObjectProperty.NullableWithDefault<EB, CTX, P>
}

internal class ObjectReaderPropertiesBuilderInstance<EB, CTX> : ObjectReaderPropertiesBuilder<EB, CTX> {
    private val properties = mutableListOf<ObjectProperty<EB, CTX>>()

    override fun <P : Any> property(
        spec: ObjectPropertySpec.Required<EB, CTX, P>
    ): ObjectProperty.Required<EB, CTX, P> =
        ObjectProperty.Required(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.Defaultable<EB, CTX, P>
    ): ObjectProperty.Defaultable<EB, CTX, P> =
        ObjectProperty.Defaultable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.Optional<EB, CTX, P>
    ): ObjectProperty.Optional<EB, CTX, P> =
        ObjectProperty.Optional(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.OptionalWithDefault<EB, CTX, P>
    ): ObjectProperty.OptionalWithDefault<EB, CTX, P> =
        ObjectProperty.OptionalWithDefault(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.Nullable<EB, CTX, P>
    ): ObjectProperty.Nullable<EB, CTX, P> =
        ObjectProperty.Nullable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.NullableWithDefault<EB, CTX, P>
    ): ObjectProperty.NullableWithDefault<EB, CTX, P> =
        ObjectProperty.NullableWithDefault(spec)
            .also { properties.add(it) }

    fun build(): ObjectProperties<EB, CTX> = ObjectProperties(properties)
}
