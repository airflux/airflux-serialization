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

package io.github.airflux.serialization.dsl.reader.struct.property

internal class PropertyValuesInstance<EB, O, CTX> : PropertyValues<EB, O, CTX> {
    private val values: MutableMap<StructProperty<EB, O, CTX>, Value> = mutableMapOf()

    override val isEmpty: Boolean
        get() = !isNotEmpty

    override val isNotEmpty: Boolean
        get() = values.any { r -> r.value is Value.Some }

    override val size: Int
        get() = values.count { r -> r.value is Value.Some }

    override operator fun <T : Any> get(property: StructProperty.NonNullable<EB, O, CTX, T>): T {
        val value = values[property]
        return if (value != null)
            @Suppress("UNCHECKED_CAST")
            (value as Value.Some).get as T
        else
            throw NoSuchElementException("Property by paths '${property.paths}' is missing in the map.")
    }

    override operator fun <T : Any> get(property: StructProperty.Nullable<EB, O, CTX, T>): T? {
        val value = values[property]
        return if (value != null) {
            @Suppress("UNCHECKED_CAST")
            if (value is Value.Some)
                value.get as T
            else
                null
        } else
            throw NoSuchElementException("Property by paths '${property.paths}' is missing in the map.")
    }

    operator fun set(property: StructProperty.NonNullable<EB, O, CTX, *>, value: Any) {
        values[property] = Value.Some(value)
    }

    operator fun set(property: StructProperty.Nullable<EB, O, CTX, *>, value: Any?) {
        if (value != null)
            values[property] = Value.Some(value)
        else
            values[property] = Value.None
    }

    internal sealed class Value {
        data class Some(val get: Any) : Value()
        object None : Value()
    }
}
