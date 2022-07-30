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
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.PathElement
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode

@Suppress("unused")
public sealed class Lookup {

    public abstract val location: Location

    public fun apply(key: String): Lookup = apply(PathElement.Key(key))
    public fun apply(idx: Int): Lookup = apply(PathElement.Idx(idx))
    public abstract fun apply(key: PathElement.Key): Lookup
    public abstract fun apply(idx: PathElement.Idx): Lookup

    public data class Defined(override val location: Location, val value: ValueNode) : Lookup() {
        override fun apply(key: PathElement.Key): Lookup = value.lookup(location, key)
        override fun apply(idx: PathElement.Idx): Lookup = value.lookup(location, idx)
    }

    public data class Undefined(override val location: Location) : Lookup() {
        override fun apply(key: PathElement.Key): Lookup = this
        override fun apply(idx: PathElement.Idx): Lookup = this
    }
}

public fun ValueNode.lookup(location: Location, key: PathElement.Key): Lookup =
    if (this is StructNode)
        this[key]
            ?.let { Lookup.Defined(location = location.append(key), value = it) }
            ?: Lookup.Undefined(location = location.append(key))
    else
        Lookup.Undefined(location = location.append(key))

public fun ValueNode.lookup(location: Location, idx: PathElement.Idx): Lookup =
    if (this is ArrayNode<*>)
        this[idx]
            ?.let { Lookup.Defined(location = location.append(idx), value = it) }
            ?: Lookup.Undefined(location = location.append(idx))
    else
        Lookup.Undefined(location = location.append(idx))

public fun ValueNode.lookup(location: Location, path: JsPath): Lookup {

    tailrec fun lookup(location: Location, path: JsPath, idxElement: Int, value: ValueNode): Lookup {
        if (idxElement == path.elements.size) return Lookup.Defined(location, value)
        return when (val element = path.elements[idxElement]) {
            is PathElement.Key -> if (value is StructNode) {
                val currentValue = value[element]
                    ?: return Lookup.Undefined(location.append(path.elements.subList(idxElement, path.elements.size)))
                lookup(location.append(element), path, idxElement + 1, currentValue)
            } else
                Lookup.Undefined(location = location.append(path.elements.subList(idxElement, path.elements.size)))

            is PathElement.Idx -> if (value is ArrayNode<*>) {
                val currentValue = value[element]
                    ?: return Lookup.Undefined(location.append(path.elements.subList(idxElement, path.elements.size)))
                lookup(location.append(element), path, idxElement + 1, currentValue)
            } else
                Lookup.Undefined(location = location.append(path.elements.subList(idxElement, path.elements.size)))
        }
    }

    return lookup(location, path, 0, this)
}
