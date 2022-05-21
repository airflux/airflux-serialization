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

class JsReaderContext private constructor(private val elements: Map<Key<*>, Element>) {

    operator fun <E : Element> plus(element: E): JsReaderContext =
        JsReaderContext(this.elements + (element.key to element))

    operator fun <E : Element> plus(elements: Iterable<E>): JsReaderContext =
        JsReaderContext(this.elements + elements.map { it.key to it })

    @Suppress("UNCHECKED_CAST")
    fun <E : Element> getOrNull(key: Key<E>): E? = elements[key]?.let { it as E }

    fun <E : Element> getValue(key: Key<E>): E = getOrNull(key)
        ?: throw NoSuchElementException("Key '${key.name}' is missing in the JsReaderContext.")

    operator fun <E : Element> contains(key: Key<E>): Boolean = elements.contains(key)

    val isEmpty: Boolean
        get() = elements.isEmpty()

    val isNotEmpty: Boolean
        get() = !isEmpty

    @Suppress("unused")
    interface Key<E : Element> {
        val name: String
    }

    interface Element {
        val key: Key<*>
        operator fun <E : Element> plus(element: E): List<Element> = listOf(this, element)
    }

    companion object {
        operator fun invoke(): JsReaderContext = JsReaderContext(emptyMap())
        operator fun invoke(element: Element): JsReaderContext = JsReaderContext(mapOf(element.key to element))
        operator fun invoke(elements: Iterable<Element>): JsReaderContext =
            JsReaderContext(elements.associateBy { it.key })
    }
}
