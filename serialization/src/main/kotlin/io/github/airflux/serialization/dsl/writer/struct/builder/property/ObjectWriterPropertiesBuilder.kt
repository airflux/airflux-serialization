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

import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.ObjectPropertySpec

public interface ObjectWriterPropertiesBuilder<T : Any> {
    public fun <P : Any> property(spec: ObjectPropertySpec.NonNullable<T, P>): ObjectProperty.NonNullable<T, P>
    public fun <P : Any> property(spec: ObjectPropertySpec.Optional<T, P>): ObjectProperty.Optional<T, P>
    public fun <P : Any> property(spec: ObjectPropertySpec.Nullable<T, P>): ObjectProperty.Nullable<T, P>
}

internal class ObjectWriterPropertiesBuilderInstance<T : Any> : ObjectWriterPropertiesBuilder<T> {
    private val properties = mutableListOf<ObjectProperty<T>>()

    override fun <P : Any> property(spec: ObjectPropertySpec.NonNullable<T, P>): ObjectProperty.NonNullable<T, P> =
        ObjectProperty.NonNullable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: ObjectPropertySpec.Optional<T, P>): ObjectProperty.Optional<T, P> =
        ObjectProperty.Optional(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: ObjectPropertySpec.Nullable<T, P>): ObjectProperty.Nullable<T, P> =
        ObjectProperty.Nullable(spec)
            .also { properties.add(it) }

    internal fun build(): ObjectProperties<T> = ObjectProperties(properties)
}
