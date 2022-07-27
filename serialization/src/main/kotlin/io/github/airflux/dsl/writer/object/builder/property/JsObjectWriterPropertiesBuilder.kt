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

package io.github.airflux.dsl.writer.`object`.builder.property

import io.github.airflux.dsl.writer.`object`.builder.property.specification.JsObjectPropertySpec

public interface JsObjectWriterPropertiesBuilder<T : Any> {
    public fun <P : Any> property(spec: JsObjectPropertySpec.NonNullable<T, P>): JsObjectProperty.NonNullable<T, P>
    public fun <P : Any> property(spec: JsObjectPropertySpec.Optional<T, P>): JsObjectProperty.Optional<T, P>
    public fun <P : Any> property(spec: JsObjectPropertySpec.Nullable<T, P>): JsObjectProperty.Nullable<T, P>
}

internal class JsObjectWriterPropertiesBuilderInstance<T : Any> : JsObjectWriterPropertiesBuilder<T> {
    private val properties = mutableListOf<JsObjectProperty<T>>()

    override fun <P : Any> property(spec: JsObjectPropertySpec.NonNullable<T, P>): JsObjectProperty.NonNullable<T, P> =
        JsObjectProperty.NonNullable(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: JsObjectPropertySpec.Optional<T, P>): JsObjectProperty.Optional<T, P> =
        JsObjectProperty.Optional(spec)
            .also { properties.add(it) }

    override fun <P : Any> property(spec: JsObjectPropertySpec.Nullable<T, P>): JsObjectProperty.Nullable<T, P> =
        JsObjectProperty.Nullable(spec)
            .also { properties.add(it) }

    internal fun build(): JsObjectProperties<T> = JsObjectProperties(properties)
}
