/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.core.reader.result

import io.github.airflux.core.path.PathElement

sealed class JsLocation {

    abstract val isEmpty: Boolean

    fun append(key: String): JsLocation = append(PathElement.Key(key))
    fun append(idx: Int): JsLocation = append(PathElement.Idx(idx))
    fun append(element: PathElement): JsLocation = Element(this, element)

    private object Empty : JsLocation() {
        override val isEmpty: Boolean = true
        override fun toString(): String = "#"
    }

    private class Element(val begin: JsLocation, val value: PathElement) : JsLocation() {

        override val isEmpty: Boolean = false

        override fun toString(): String = buildString {
            append("#")
            foldLeft(this, this@Element) { acc, value -> acc.append(value) }
        }

        override fun hashCode(): Int = foldRight(7, this) { v, p -> v * 31 + p.hashCode() }

        override fun equals(other: Any?): Boolean {
            tailrec fun listEq(self: JsLocation, other: JsLocation): Boolean = when {
                self is Element && other is Element ->
                    if (self.value == other.value) listEq(self.begin, other.begin) else false
                self is Empty && other is Empty -> true
                else -> false
            }

            return this === other || (other is JsLocation && listEq(this, other))
        }
    }

    companion object {

        val empty: JsLocation = Empty

        tailrec fun <R> foldRight(initial: R, location: JsLocation, operation: (R, PathElement) -> R): R =
            when (location) {
                is Empty -> initial
                is Element -> foldRight(operation(initial, location.value), location.begin, operation)
            }

        fun <R> foldLeft(initial: R, location: JsLocation, operation: (R, PathElement) -> R): R =
            when (location) {
                is Empty -> initial
                is Element -> operation(foldLeft(initial, location.begin, operation), location.value)
            }
    }
}
