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

package io.github.airflux.serialization.core.location

import io.github.airflux.serialization.core.path.PropertyPath

public sealed class Location {

    public abstract val isEmpty: Boolean

    public fun append(key: String): Location = append(PropertyPath.Element.Key(key))
    public fun append(idx: Int): Location = append(PropertyPath.Element.Idx(idx))
    public fun append(element: PropertyPath.Element): Location = Element(this, element)
    public fun append(path: PropertyPath): Location =
        path.foldLeft(this) { location, element -> location.append(element) }

    public fun <R> foldLeft(initial: R, operation: (R, PropertyPath.Element) -> R): R {
        tailrec fun <R> foldLeft(initial: R, location: Location, operation: (R, PropertyPath.Element) -> R): R =
            when (location) {
                is Empty -> initial
                is Element -> foldLeft(operation(initial, location.value), location.begin, operation)
            }

        return foldLeft(initial, this, operation)
    }

    public fun <R> foldRight(initial: R, operation: (R, PropertyPath.Element) -> R): R {
        fun <R> foldRight(initial: R, location: Location, operation: (R, PropertyPath.Element) -> R): R =
            when (location) {
                is Empty -> initial
                is Element -> operation(foldRight(initial, location.begin, operation), location.value)
            }
        return foldRight(initial, this, operation)
    }

    private class Element(val begin: Location, val value: PropertyPath.Element) : Location() {

        override val isEmpty: Boolean = false

        override fun toString(): String = buildString {
            append("#")
            this@Element.foldRight(this) { acc, value -> acc.append(value) }
        }

        override fun hashCode(): Int = foldLeft(7) { v, p -> v * 31 + p.hashCode() }

        override fun equals(other: Any?): Boolean {
            tailrec fun equals(self: Location, other: Location): Boolean = when {
                self is Element && other is Element ->
                    if (self.value == other.value) equals(self.begin, other.begin) else false

                self is Empty && other is Empty -> true
                else -> false
            }

            return this === other || (other is Location && equals(this, other))
        }
    }

    public companion object Empty : Location() {
        override val isEmpty: Boolean = true
        override fun toString(): String = "#"
    }
}
