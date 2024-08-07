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

package io.github.airflux.serialization.core.location

import io.github.airflux.serialization.core.path.JsPath

public sealed interface JsLocation {

    public val isEmpty: Boolean

    public fun append(key: String): JsLocation = append(JsPath.Element.Key(key))
    public fun append(idx: Int): JsLocation = append(JsPath.Element.Idx(idx))
    public fun append(element: JsPath.Element): JsLocation = Element(element, this)
    public fun append(path: JsPath): JsLocation =
        path.foldLeft(this) { location, element -> location.append(element) }

    public fun <R> foldLeft(initial: R, operation: (R, JsPath.Element) -> R): R {
        tailrec fun <R> foldLeft(initial: R, location: JsLocation, operation: (R, JsPath.Element) -> R): R =
            when (location) {
                is Root -> initial
                is Element -> foldLeft(operation(initial, location.head), location.tail, operation)
            }

        return foldLeft(initial, this, operation)
    }

    public fun <R> foldRight(initial: R, operation: (R, JsPath.Element) -> R): R {
        fun <R> foldRight(initial: R, location: JsLocation, operation: (R, JsPath.Element) -> R): R =
            when (location) {
                is Root -> initial
                is Element -> operation(foldRight(initial, location.tail, operation), location.head)
            }
        return foldRight(initial, this, operation)
    }

    private class Element(val head: JsPath.Element, val tail: JsLocation) : JsLocation {

        override val isEmpty: Boolean = false

        override fun toString(): String = buildString {
            append("#")
            this@Element.foldRight(this) { acc, value -> acc.append(value) }
        }

        override fun hashCode(): Int = foldLeft(7) { v, p -> v * 31 + p.hashCode() }

        override fun equals(other: Any?): Boolean {
            tailrec fun equals(self: JsLocation, other: JsLocation): Boolean = when {
                self is Element && other is Element ->
                    if (self.head == other.head) equals(self.tail, other.tail) else NOT_EQUAL

                self is Root && other is Root -> EQUAL
                else -> NOT_EQUAL
            }

            return this === other || (other is JsLocation && equals(this, other))
        }
    }

    public companion object Root : JsLocation {
        override val isEmpty: Boolean = true
        override fun toString(): String = "#"

        private const val EQUAL = true
        private const val NOT_EQUAL = false
    }
}
