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

package io.github.airflux.serialization.core.location

import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPathElement

public sealed class Location {

    public abstract val isEmpty: Boolean

    public fun append(key: String): Location = append(PropertyPathElement.Key(key))
    public fun append(idx: Int): Location = append(PropertyPathElement.Idx(idx))
    public fun append(element: PropertyPathElement): Location = Element(this, element)
    public fun append(path: PropertyPath): Location = path.elements.fold(this) { acc, p -> acc.append(p) }

    public fun append(elements: Iterable<PropertyPathElement>): Location =
        elements.fold(this) { location, pathElement ->
            location.append(pathElement)
        }

    private object Empty : Location() {
        override val isEmpty: Boolean = true
        override fun toString(): String = "#"
    }

    private class Element(val begin: Location, val value: PropertyPathElement) : Location() {

        override val isEmpty: Boolean = false

        override fun toString(): String = buildString {
            append("#")
            foldLeft(this, this@Element) { acc, value -> acc.append(value) }
        }

        override fun hashCode(): Int = foldRight(7, this) { v, p -> v * 31 + p.hashCode() }

        override fun equals(other: Any?): Boolean {
            tailrec fun listEq(self: Location, other: Location): Boolean = when {
                self is Element && other is Element ->
                    if (self.value == other.value) listEq(self.begin, other.begin) else false

                self is Empty && other is Empty -> true
                else -> false
            }

            return this === other || (other is Location && listEq(this, other))
        }
    }

    public companion object {

        public val empty: Location = Empty

        public tailrec fun <R> foldRight(initial: R, location: Location, operation: (R, PropertyPathElement) -> R): R =
            when (location) {
                is Empty -> initial
                is Element -> foldRight(operation(initial, location.value), location.begin, operation)
            }

        public fun <R> foldLeft(initial: R, location: Location, operation: (R, PropertyPathElement) -> R): R =
            when (location) {
                is Empty -> initial
                is Element -> operation(foldLeft(initial, location.begin, operation), location.value)
            }
    }
}
