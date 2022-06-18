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

package io.github.airflux.core.writer.context

public class JsWriterContext private constructor(private val elements: Map<Key<*>, Element>) {

    public operator fun <E : Element> plus(element: E): JsWriterContext =
        JsWriterContext(this.elements + (element.key to element))

    public operator fun <E : Element> plus(elements: Iterable<E>): JsWriterContext =
        JsWriterContext(this.elements + elements.map { it.key to it })

    @Suppress("UNCHECKED_CAST")
    public fun <E : Element> getOrNull(key: Key<E>): E? = elements[key]?.let { it as E }

    public fun <E : Element> getValue(key: Key<E>): E = getOrNull(key)
        ?: throw NoSuchElementException("Key '${key.name}' is missing in the context of writing.")

    public operator fun <E : Element> contains(key: Key<E>): Boolean = elements.contains(key)

    public val isEmpty: Boolean
        get() = elements.isEmpty()

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public interface Key<E : Element> {
        public val name: String
    }

    public interface Element {
        public val key: Key<*>
        public operator fun <E : Element> plus(element: E): List<Element> = listOf(this, element)
    }

    public companion object {
        public operator fun invoke(): JsWriterContext = JsWriterContext(emptyMap())
        public operator fun invoke(element: Element): JsWriterContext = JsWriterContext(mapOf(element.key to element))
        public operator fun invoke(elements: Iterable<Element>): JsWriterContext =
            JsWriterContext(elements.associateBy { it.key })
    }
}
