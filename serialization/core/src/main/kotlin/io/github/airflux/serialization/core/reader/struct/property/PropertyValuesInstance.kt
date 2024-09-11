/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.struct.property

internal class PropertyValuesInstance<EB, O> : PropertyValues<EB, O> {
    private val values: MutableMap<StructProperty<EB, O, *>, Value> = mutableMapOf()

    override val isEmpty: Boolean
        get() = !isNotEmpty

    override val isNotEmpty: Boolean
        get() = values.any { r -> r.value is Value.Some }

    override val size: Int
        get() = values.count { r -> r.value is Value.Some }

    override operator fun <T> get(property: StructProperty<EB, O, T>): T {
        val value: Value? = values[property]
        return if (value != null) {
            @Suppress("UNCHECKED_CAST")
            value.orNull as T
        } else
            throw NoSuchElementException("Property by paths '${property.paths}' is missing in the map.")
    }

    operator fun set(property: StructProperty<EB, O, *>, value: Any?) {
        if (value != null)
            values[property] = Value.Some(value)
        else
            values[property] = Value.None
    }

    internal sealed interface Value {
        val orNull: Any?

        class Some(value: Any) : Value {
            override val orNull: Any = value
        }

        object None : Value {
            override val orNull: Any? = null
        }
    }
}
