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

package io.github.airflux.serialization.core.lookup

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPath.Element
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode

@Suppress("unused")
public sealed class Lookup {

    public abstract val location: Location

    public fun apply(key: String): Lookup = apply(Element.Key(key))
    public fun apply(idx: Int): Lookup = apply(Element.Idx(idx))
    public abstract fun apply(key: Element.Key): Lookup
    public abstract fun apply(idx: Element.Idx): Lookup

    public data class Defined(override val location: Location, val value: ValueNode) : Lookup() {
        override fun apply(key: Element.Key): Lookup = value.lookup(location, key)
        override fun apply(idx: Element.Idx): Lookup = value.lookup(location, idx)
    }

    public data class Undefined(override val location: Location) : Lookup() {
        override fun apply(key: Element.Key): Lookup = this
        override fun apply(idx: Element.Idx): Lookup = this
    }
}

public fun ValueNode.lookup(location: Location, key: Element.Key): Lookup =
    this.lookup(key)
        ?.let { Lookup.Defined(location = location.append(key), value = it) }
        ?: Lookup.Undefined(location = location.append(key))

public fun ValueNode.lookup(location: Location, idx: Element.Idx): Lookup =
    this.lookup(idx)
        ?.let { Lookup.Defined(location = location.append(idx), value = it) }
        ?: Lookup.Undefined(location = location.append(idx))

public fun ValueNode.lookup(location: Location, path: PropertyPath): Lookup =
    this.lookup(path)
        ?.let { Lookup.Defined(location = location.append(path), value = it) }
        ?: Lookup.Undefined(location = location.append(path))

internal fun ValueNode.lookup(key: Element.Key): ValueNode? = if (this is StructNode) this[key] else null

internal fun ValueNode.lookup(idx: Element.Idx): ValueNode? = if (this is ArrayNode<*>) this[idx] else null

internal fun ValueNode.lookup(path: PropertyPath): ValueNode? {
    tailrec fun lookup(path: PropertyPath, idxElement: Int, value: ValueNode?): ValueNode? {
        if (value == null || idxElement == path.elements.size) return value
        return when (val element = path.elements[idxElement]) {
            is Element.Key -> if (value is StructNode) lookup(path, idxElement + 1, value[element]) else null
            is Element.Idx -> if (value is ArrayNode<*>) lookup(path, idxElement + 1, value[element]) else null
        }
    }

    return lookup(path, 0, this)
}
