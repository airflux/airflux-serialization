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

package io.github.airflux.serialization.core.context

public sealed interface JsContext {

    public val isEmpty: Boolean

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public operator fun <E : Element> get(key: Key<E>): E?

    public operator fun <E : Element> contains(key: Key<E>): Boolean

    public operator fun <E : Element> plus(element: E): JsContext = Combined(head = element, tail = this)

    public fun <R> fold(initial: R, operation: (R, Element) -> R): R

    public interface Key<E : Element>

    public interface Element : JsContext {
        public val key: Key<*>

        override val isEmpty: Boolean
            get() = false

        override fun <E : Element> get(key: Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            if (this.key == key) this as E else null

        override fun <E : Element> contains(key: Key<E>): Boolean = this.key == key

        public override fun <R> fold(initial: R, operation: (R, Element) -> R): R = operation(initial, this)
    }

    public data object Empty : JsContext {
        override val isEmpty: Boolean = true
        override fun <E : Element> get(key: Key<E>): E? = null
        override fun <E : Element> contains(key: Key<E>): Boolean = NOT_CONTAINS
        override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial
    }

    private class Combined(val head: Element, val tail: JsContext) : JsContext {

        override val isEmpty: Boolean = false

        override fun <E : Element> get(key: Key<E>): E? {
            tailrec fun <E : Element> get(key: Key<E>, element: JsContext): E? = when (element) {
                is Combined -> {
                    val value = element.head[key]
                    if (value != null) value else get(key, element.tail)
                }

                is Empty,
                is Element -> element[key]
            }

            return get(key, this)
        }

        override fun <E : Element> contains(key: Key<E>): Boolean {
            tailrec fun <E : Element> contains(key: Key<E>, element: JsContext): Boolean =
                when (element) {
                    is Empty -> key in element
                    is Element -> key in element
                    is Combined -> if (key in element.head) CONTAINS else contains(key, element.tail)
                }

            return contains(key, this)
        }

        override fun <R> fold(initial: R, operation: (R, Element) -> R): R {

            tailrec fun <R> fold(initial: R, element: JsContext, operation: (R, Element) -> R): R =
                when (element) {
                    is Empty -> initial
                    is Element -> operation(initial, element)
                    is Combined -> fold(operation(initial, element.head), element.tail, operation)
                }

            return fold(initial, this, operation)
        }
    }

    public companion object {
        private const val CONTAINS = true
        private const val NOT_CONTAINS = false
    }
}
