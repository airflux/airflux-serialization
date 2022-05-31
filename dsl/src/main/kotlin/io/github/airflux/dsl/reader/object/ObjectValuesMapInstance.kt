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

package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.property.JsObjectReaderProperty

internal class ObjectValuesMapInstance : ObjectValuesMap {
    private val items: MutableMap<JsObjectReaderProperty, Any?> = mutableMapOf()

    override val isEmpty: Boolean
        get() = items.isEmpty()

    override val isNotEmpty: Boolean
        get() = items.isNotEmpty()

    override val size: Int
        get() = items.size

    override infix operator fun <T : Any> get(property: JsObjectReaderProperty.Required<T>): T =
        items.getNonNullable(property)

    override infix operator fun <T : Any> get(property: JsObjectReaderProperty.Defaultable<T>): T =
        items.getNonNullable(property)

    override infix operator fun <T : Any> get(property: JsObjectReaderProperty.Optional<T>): T? =
        items.getNullable(property)

    override infix operator fun <T : Any> get(property: JsObjectReaderProperty.OptionalWithDefault<T>): T =
        items.getNonNullable(property)

    override infix operator fun <T : Any> get(property: JsObjectReaderProperty.Nullable<T>): T? =
        items.getNullable(property)

    override infix operator fun <T : Any> get(property: JsObjectReaderProperty.NullableWithDefault<T>): T? =
        items.getNullable(property)

    operator fun set(property: JsObjectReaderProperty, value: Any?) {
        items[property] = value
    }

    companion object {

        private fun <T> Map<JsObjectReaderProperty, Any?>.getNonNullable(property: JsObjectReaderProperty): T =
            getValue(property)
                .let {
                    @Suppress("UNCHECKED_CAST")
                    checkNotNull(it) as T
                }

        private fun <T> Map<JsObjectReaderProperty, Any?>.getNullable(property: JsObjectReaderProperty): T? =
            getValue(property)
                ?.let {
                    @Suppress("UNCHECKED_CAST")
                    it as T
                }
    }
}
