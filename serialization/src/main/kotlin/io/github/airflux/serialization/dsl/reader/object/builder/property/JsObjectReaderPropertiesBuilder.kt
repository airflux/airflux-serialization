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

package io.github.airflux.serialization.dsl.reader.`object`.builder.property

import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.ObjectPropertySpec

public interface JsObjectReaderPropertiesBuilder {

    public fun <P : Any> property(spec: ObjectPropertySpec.Required<P>): JsObjectProperty.Required<P>

    public fun <P : Any> property(spec: ObjectPropertySpec.Defaultable<P>): JsObjectProperty.Defaultable<P>

    public fun <P : Any> property(spec: ObjectPropertySpec.Optional<P>): JsObjectProperty.Optional<P>

    public fun <P : Any> property(
        spec: ObjectPropertySpec.OptionalWithDefault<P>
    ): JsObjectProperty.OptionalWithDefault<P>

    public fun <P : Any> property(spec: ObjectPropertySpec.Nullable<P>): JsObjectProperty.Nullable<P>

    public fun <P : Any> property(
        spec: ObjectPropertySpec.NullableWithDefault<P>
    ): JsObjectProperty.NullableWithDefault<P>
}

internal class JsObjectReaderPropertiesBuilderInstance : JsObjectReaderPropertiesBuilder {
    private val properties = mutableListOf<JsObjectProperty>()

    override fun <P : Any> property(spec: ObjectPropertySpec.Required<P>): JsObjectProperty.Required<P> =
        JsObjectProperty.Required(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: ObjectPropertySpec.Defaultable<P>): JsObjectProperty.Defaultable<P> =
        JsObjectProperty.Defaultable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: ObjectPropertySpec.Optional<P>): JsObjectProperty.Optional<P> =
        JsObjectProperty.Optional(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.OptionalWithDefault<P>
    ): JsObjectProperty.OptionalWithDefault<P> =
        JsObjectProperty.OptionalWithDefault(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: ObjectPropertySpec.Nullable<P>): JsObjectProperty.Nullable<P> =
        JsObjectProperty.Nullable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(
        spec: ObjectPropertySpec.NullableWithDefault<P>
    ): JsObjectProperty.NullableWithDefault<P> =
        JsObjectProperty.NullableWithDefault(spec)
            .also { properties.add(it) }

    fun build(): JsObjectProperties = JsObjectProperties(properties)
}
