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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPath.Element
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode

public sealed class LookupResult {

    public fun apply(key: String): LookupResult = apply(Element.Key(key))
    public fun apply(idx: Int): LookupResult = apply(Element.Idx(idx))
    public abstract fun apply(key: Element.Key): LookupResult
    public abstract fun apply(idx: Element.Idx): LookupResult

    public data class Defined(val location: Location, val value: ValueNode) : LookupResult() {
        override fun apply(key: Element.Key): LookupResult = value.lookup(location, key)
        override fun apply(idx: Element.Idx): LookupResult = value.lookup(location, idx)
    }

    public sealed class Undefined : LookupResult() {

        public data class PathMissing(val location: Location) : Undefined() {
            override fun apply(key: Element.Key): LookupResult = PathMissing(location = this.location.append(key))
            override fun apply(idx: Element.Idx): LookupResult = PathMissing(location = this.location.append(idx))
        }

        public data class InvalidType(
            public val expected: Iterable<String>,
            public val actual: String,
            val breakpoint: Location
        ) : Undefined() {
            override fun apply(key: Element.Key): LookupResult = this
            override fun apply(idx: Element.Idx): LookupResult = this
        }
    }
}

public fun ValueNode.lookup(location: Location, key: Element.Key): LookupResult =
    if (this is StructNode)
        this[key]
            ?.let { LookupResult.Defined(location = location.append(key), value = it) }
            ?: LookupResult.Undefined.PathMissing(location = location.append(key))
    else
        LookupResult.Undefined.InvalidType(
            expected = listOf(StructNode.nameOfType),
            actual = this.nameOfType,
            breakpoint = location
        )

public fun ValueNode.lookup(location: Location, idx: Element.Idx): LookupResult =
    if (this is ArrayNode<*>)
        this[idx]
            ?.let { LookupResult.Defined(location = location.append(idx), value = it) }
            ?: LookupResult.Undefined.PathMissing(location = location.append(idx))
    else
        LookupResult.Undefined.InvalidType(
            expected = listOf(ArrayNode.nameOfType),
            actual = this.nameOfType,
            breakpoint = location
        )

public fun ValueNode.lookup(location: Location, path: PropertyPath): LookupResult {
    tailrec fun lookup(location: Location, path: PropertyPath?, source: ValueNode): LookupResult {
        return if (path != null) {
            when (val element = path.head) {
                is Element.Key -> if (source is StructNode) {
                    val value = source[element]
                        ?: return LookupResult.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    LookupResult.Undefined.InvalidType(
                        expected = listOf(StructNode.nameOfType),
                        actual = source.nameOfType,
                        breakpoint = location
                    )

                is Element.Idx -> if (source is ArrayNode<*>) {
                    val value = source[element]
                        ?: return LookupResult.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    LookupResult.Undefined.InvalidType(
                        expected = listOf(ArrayNode.nameOfType),
                        actual = source.nameOfType,
                        breakpoint = location
                    )
            }
        } else
            LookupResult.Defined(location = location, value = source)
    }

    return lookup(location, path, this)
}
