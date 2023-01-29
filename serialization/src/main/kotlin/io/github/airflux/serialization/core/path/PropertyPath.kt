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

public sealed class PropertyPath {

    public abstract val head: Element
    public abstract val tail: PropertyPath?

    public fun append(key: String): PropertyPath = append(Element.Key(key))
    public fun append(idx: Int): PropertyPath = append(Element.Idx(idx))
    public fun append(element: Element): PropertyPath =
        foldRight(PropertyPath(element)) { acc, item -> Multiple(item, acc) }

    public fun <R> foldLeft(initial: R, operation: (R, Element) -> R): R {
        tailrec fun <R> foldLeft(initial: R, path: PropertyPath?, operation: (R, Element) -> R): R =
            if (path == null) initial else foldLeft(operation(initial, path.head), path.tail, operation)

        return foldLeft(initial, this, operation)
    }

    public fun <R> foldRight(initial: R, operation: (R, Element) -> R): R {
        fun <R> foldRight(initial: R, path: PropertyPath?, operation: (R, Element) -> R): R =
            if (path == null) initial else operation(foldRight(initial, path.tail, operation), path.head)

        return foldRight(initial, this, operation)
    }

    override fun toString(): String = buildString {
        append("#")
        this@PropertyPath.foldLeft(this) { acc, value -> acc.append(value) }
    }

    override fun hashCode(): Int = foldLeft(7) { acc, item -> acc * 31 + item.hashCode() }

    override fun equals(other: Any?): Boolean {
        tailrec fun equals(self: PropertyPath?, other: PropertyPath?): Boolean =
            when {
                self != null && other != null -> if (self.head == other.head) equals(self.tail, other.tail) else false
                self == null && other == null -> true
                else -> false
            }
        return this === other || (other is PropertyPath && equals(this, other))
    }

    private class Single(override val head: Element) : PropertyPath() {
        override val tail: PropertyPath?
            get() = null
    }

    private class Multiple(override val head: Element, override val tail: PropertyPath?) : PropertyPath()

    public sealed class Element {

        public data class Key(val get: String) : Element() {
            override fun toString(): String = "/$get"
        }

        public data class Idx(val get: Int) : Element() {
            override fun toString(): String = "[$get]"
        }
    }

    public companion object {
        public operator fun invoke(key: String): PropertyPath = Single(Element.Key(key))
        public operator fun invoke(idx: Int): PropertyPath = Single(Element.Idx(idx))
        public operator fun invoke(element: Element): PropertyPath = Single(element)
    }
}
