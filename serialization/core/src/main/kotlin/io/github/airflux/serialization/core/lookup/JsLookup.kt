/*
 * Copyright 2021-2023 Maxim Sambulat.
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
import io.github.airflux.serialization.core.path.JsPath.Element
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode

public sealed class JsLookup {

    public fun apply(key: String): JsLookup = apply(Element.Key(key))
    public fun apply(idx: Int): JsLookup = apply(Element.Idx(idx))
    public abstract fun apply(key: Element.Key): JsLookup
    public abstract fun apply(idx: Element.Idx): JsLookup

    public data class Defined(val location: JsLocation, val value: ValueNode) : JsLookup() {
        override fun apply(key: Element.Key): JsLookup = value.lookup(location, key)
        override fun apply(idx: Element.Idx): JsLookup = value.lookup(location, idx)
    }

    public sealed class Undefined : JsLookup() {

        public data class PathMissing(val location: JsLocation) : Undefined() {
            override fun apply(key: Element.Key): JsLookup = PathMissing(location = this.location.append(key))
            override fun apply(idx: Element.Idx): JsLookup = PathMissing(location = this.location.append(idx))
        }

        public data class InvalidType(
            public val expected: Iterable<String>,
            public val actual: String,
            val breakpoint: JsLocation
        ) : Undefined() {
            override fun apply(key: Element.Key): JsLookup = this
            override fun apply(idx: Element.Idx): JsLookup = this
        }
    }
}

public fun ValueNode.lookup(location: JsLocation, key: Element.Key): JsLookup =
    if (this is StructNode)
        this[key]
            ?.let { JsLookup.Defined(location = location.append(key), value = it) }
            ?: JsLookup.Undefined.PathMissing(location = location.append(key))
    else
        JsLookup.Undefined.InvalidType(
            expected = listOf(StructNode.nameOfType),
            actual = this.nameOfType,
            breakpoint = location
        )

public fun ValueNode.lookup(location: JsLocation, idx: Element.Idx): JsLookup =
    if (this is ArrayNode)
        this[idx]
            ?.let { JsLookup.Defined(location = location.append(idx), value = it) }
            ?: JsLookup.Undefined.PathMissing(location = location.append(idx))
    else
        JsLookup.Undefined.InvalidType(
            expected = listOf(ArrayNode.nameOfType),
            actual = this.nameOfType,
            breakpoint = location
        )

public fun ValueNode.lookup(location: JsLocation, path: JsPath): JsLookup {
    tailrec fun lookup(location: JsLocation, path: JsPath?, source: ValueNode): JsLookup {
        return if (path != null) {
            when (val element = path.head) {
                is Element.Key -> if (source is StructNode) {
                    val value = source[element]
                        ?: return JsLookup.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    JsLookup.Undefined.InvalidType(
                        expected = listOf(StructNode.nameOfType),
                        actual = source.nameOfType,
                        breakpoint = location
                    )

                is Element.Idx -> if (source is ArrayNode) {
                    val value = source[element]
                        ?: return JsLookup.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    JsLookup.Undefined.InvalidType(
                        expected = listOf(ArrayNode.nameOfType),
                        actual = source.nameOfType,
                        breakpoint = location
                    )
            }
        } else
            JsLookup.Defined(location = location, value = source)
    }

    return lookup(location, path, this)
}
