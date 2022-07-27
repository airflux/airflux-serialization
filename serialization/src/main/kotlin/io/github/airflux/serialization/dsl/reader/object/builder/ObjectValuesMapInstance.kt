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

package io.github.airflux.serialization.dsl.reader.`object`.builder

import io.github.airflux.serialization.dsl.reader.`object`.builder.property.JsObjectProperty

internal class ObjectValuesMapInstance : ObjectValuesMap {
    private val properties: MutableSet<JsObjectProperty> = mutableSetOf()
    private val values: MutableMap<JsObjectProperty, Any> = mutableMapOf()

    override val isEmpty: Boolean
        get() = values.isEmpty()

    override val isNotEmpty: Boolean
        get() = values.isNotEmpty()

    override val size: Int
        get() = values.size

    override operator fun <T : Any> get(property: JsObjectProperty.Required<T>): T = getNonNullable(property)
    override operator fun <T : Any> get(property: JsObjectProperty.Defaultable<T>): T = getNonNullable(property)
    override operator fun <T : Any> get(property: JsObjectProperty.Optional<T>): T? = getNullable(property)
    override operator fun <T : Any> get(property: JsObjectProperty.OptionalWithDefault<T>): T = getNonNullable(property)
    override operator fun <T : Any> get(property: JsObjectProperty.Nullable<T>): T? = getNullable(property)
    override operator fun <T : Any> get(property: JsObjectProperty.NullableWithDefault<T>): T? = getNullable(property)

    operator fun set(property: JsObjectProperty, value: Any?) {
        if (value != null) values[property] = value
        properties.add(property)
    }

    private fun <T> getNonNullable(property: JsObjectProperty): T {
        val value = values[property]
        return if (value != null)
            @Suppress("UNCHECKED_CAST")
            value as T
        else
            throw NoSuchElementException("Property by path '${property.path}' is missing in the map.")
    }

    private fun <T> getNullable(property: JsObjectProperty): T? {
        val value = values[property]
        return if (value != null)
            @Suppress("UNCHECKED_CAST")
            value as T
        else {
            if (property in properties)
                null
            else
                throw NoSuchElementException("Property by path '${property.path}' is missing in the map.")
        }
    }
}
