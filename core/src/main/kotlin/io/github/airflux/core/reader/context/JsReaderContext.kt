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

package io.github.airflux.core.reader.context

public class JsReaderContext private constructor(private val elements: Map<Key<*>, Element>) {

    public operator fun <E : Element> plus(element: E): JsReaderContext =
        JsReaderContext(this.elements + (element.key to element))

    public operator fun <E : Element> plus(elements: Iterable<E>): JsReaderContext =
        JsReaderContext(this.elements + elements.map { it.key to it })

    @Suppress("UNCHECKED_CAST")
    public fun <E : Element> getOrNull(key: Key<E>): E? = elements[key]?.let { it as E }

    public fun <E : Element> getValue(key: Key<E>): E = getOrNull(key)
        ?: throw NoSuchElementException("Key '${key.name}' is missing in the JsReaderContext.")

    public operator fun <E : Element> contains(key: Key<E>): Boolean = elements.contains(key)

    public val isEmpty: Boolean
        get() = elements.isEmpty()

    public val isNotEmpty: Boolean
        get() = !isEmpty

    @Suppress("unused")
    public interface Key<E : Element> {
        public val name: String
    }

    public interface Element {
        public val key: Key<*>
        public operator fun <E : Element> plus(element: E): List<Element> = listOf(this, element)
    }

    public companion object {
        public operator fun invoke(): JsReaderContext = JsReaderContext(emptyMap())
        public operator fun invoke(element: Element): JsReaderContext = JsReaderContext(mapOf(element.key to element))
        public operator fun invoke(elements: Iterable<Element>): JsReaderContext =
            JsReaderContext(elements.associateBy { it.key })
    }
}
