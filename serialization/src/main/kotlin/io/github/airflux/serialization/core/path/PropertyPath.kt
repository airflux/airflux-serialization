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

package io.github.airflux.serialization.core.path

@Suppress("unused")
public class PropertyPath private constructor(public val elements: List<Element>) {

    public constructor(key: String) : this(Element.Key(key))
    public constructor(idx: Int) : this(Element.Idx(idx))
    public constructor(element: Element) : this(listOf(element))

    public fun append(key: String): PropertyPath = append(Element.Key(key))
    public fun append(idx: Int): PropertyPath = append(Element.Idx(idx))
    public fun append(element: Element): PropertyPath = PropertyPath(elements + element)

    override fun toString(): String = buildString {
        append("#")
        elements.forEach { element -> append(element) }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is PropertyPath && this.elements == other.elements)

    override fun hashCode(): Int = elements.hashCode()

    public sealed class Element {

        public data class Key(val get: String) : Element() {
            override fun toString(): String = "/$get"
        }

        public data class Idx(val get: Int) : Element() {
            override fun toString(): String = "[$get]"
        }
    }

    public companion object
}
