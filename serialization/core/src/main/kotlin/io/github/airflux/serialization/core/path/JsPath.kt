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

package io.github.airflux.serialization.core.path

public sealed interface JsPath {

    public val head: Element
    public val tail: JsPath?

    public fun append(key: String): JsPath = append(Element.Key(key))
    public fun append(idx: Int): JsPath = append(Element.Idx(idx))
    public fun append(element: Element): JsPath =
        foldRight(JsPath(element)) { acc, item -> Multiple(item, acc) }

    public fun <R> foldLeft(initial: R, operation: (R, Element) -> R): R
    public fun <R> foldRight(initial: R, operation: (R, Element) -> R): R

    public sealed class Element {

        public data class Key(val get: String) : Element() {
            override fun toString(): String = "/$get"
        }

        public data class Idx(val get: Int) : Element() {
            override fun toString(): String = "[$get]"
        }
    }

    private class Single(override val head: Element) : JsPath {

        override val tail: JsPath?
            get() = null

        override fun <R> foldLeft(initial: R, operation: (R, Element) -> R): R = operation(initial, head)

        override fun <R> foldRight(initial: R, operation: (R, Element) -> R): R = operation(initial, head)

        override fun toString(): String = "#$head"

        override fun hashCode(): Int = head.hashCode()

        override fun equals(other: Any?): Boolean = this === other || (other is Single && this.head == other.head)
    }

    private class Multiple(override val head: Element, override val tail: JsPath?) : JsPath {

        override fun <R> foldLeft(initial: R, operation: (R, Element) -> R): R {
            tailrec fun <R> foldLeft(initial: R, path: JsPath?, operation: (R, Element) -> R): R =
                if (path == null) initial else foldLeft(operation(initial, path.head), path.tail, operation)

            return foldLeft(initial, this, operation)
        }

        override fun <R> foldRight(initial: R, operation: (R, Element) -> R): R {
            fun <R> foldRight(initial: R, path: JsPath?, operation: (R, Element) -> R): R =
                if (path == null) initial else operation(foldRight(initial, path.tail, operation), path.head)

            return foldRight(initial, this, operation)
        }

        override fun toString(): String = buildString {
            append("#")
            this@Multiple.foldLeft(this) { acc, value -> acc.append(value) }
        }

        override fun hashCode(): Int = foldLeft(7) { acc, item -> acc * 31 + item.hashCode() }

        override fun equals(other: Any?): Boolean {
            tailrec fun equals(self: JsPath?, other: JsPath?): Boolean =
                when {
                    self != null && other != null ->
                        if (self.head == other.head) equals(self.tail, other.tail) else false

                    self == null && other == null -> true
                    else -> false
                }
            return this === other || (other is Multiple && equals(this, other))
        }
    }

    public companion object {

        @JvmStatic
        public operator fun invoke(key: String): JsPath = Single(Element.Key(key))

        @JvmStatic
        public operator fun invoke(idx: Int): JsPath = Single(Element.Idx(idx))

        @JvmStatic
        public operator fun invoke(element: Element): JsPath = Single(element)
    }
}
