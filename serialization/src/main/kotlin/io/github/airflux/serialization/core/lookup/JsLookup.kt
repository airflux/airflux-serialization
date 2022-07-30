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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.PathElement
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode

@Suppress("unused")
public sealed class JsLookup {

    public abstract val location: JsLocation

    public fun apply(key: String): JsLookup = apply(PathElement.Key(key))
    public fun apply(idx: Int): JsLookup = apply(PathElement.Idx(idx))
    public abstract fun apply(key: PathElement.Key): JsLookup
    public abstract fun apply(idx: PathElement.Idx): JsLookup

    public data class Defined(override val location: JsLocation, val value: ValueNode) : JsLookup() {
        override fun apply(key: PathElement.Key): JsLookup = value.lookup(location, key)
        override fun apply(idx: PathElement.Idx): JsLookup = value.lookup(location, idx)
    }

    public data class Undefined(override val location: JsLocation) : JsLookup() {
        override fun apply(key: PathElement.Key): JsLookup = this
        override fun apply(idx: PathElement.Idx): JsLookup = this
    }
}

public fun ValueNode.lookup(location: JsLocation, key: PathElement.Key): JsLookup =
    if (this is StructNode)
        this[key]
            ?.let { JsLookup.Defined(location = location.append(key), value = it) }
            ?: JsLookup.Undefined(location = location.append(key))
    else
        JsLookup.Undefined(location = location.append(key))

public fun ValueNode.lookup(location: JsLocation, idx: PathElement.Idx): JsLookup =
    if (this is ArrayNode<*>)
        this[idx]
            ?.let { JsLookup.Defined(location = location.append(idx), value = it) }
            ?: JsLookup.Undefined(location = location.append(idx))
    else
        JsLookup.Undefined(location = location.append(idx))

public fun ValueNode.lookup(location: JsLocation, path: JsPath): JsLookup {

    tailrec fun lookup(location: JsLocation, path: JsPath, idxElement: Int, value: ValueNode): JsLookup {
        if (idxElement == path.elements.size) return JsLookup.Defined(location, value)
        return when (val element = path.elements[idxElement]) {
            is PathElement.Key -> if (value is StructNode) {
                val currentValue = value[element]
                    ?: return JsLookup.Undefined(location.append(path.elements.subList(idxElement, path.elements.size)))
                lookup(location.append(element), path, idxElement + 1, currentValue)
            } else
                JsLookup.Undefined(location = location.append(path.elements.subList(idxElement, path.elements.size)))

            is PathElement.Idx -> if (value is ArrayNode<*>) {
                val currentValue = value[element]
                    ?: return JsLookup.Undefined(location.append(path.elements.subList(idxElement, path.elements.size)))
                lookup(location.append(element), path, idxElement + 1, currentValue)
            } else
                JsLookup.Undefined(location = location.append(path.elements.subList(idxElement, path.elements.size)))
        }
    }

    return lookup(location, path, 0, this)
}
