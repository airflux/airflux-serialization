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

package io.github.airflux.core.reader.context

sealed interface JsReaderContext {
    operator fun <E : Element> plus(element: E): JsReaderContext
    operator fun <E : Element> get(key: Key<E>): E?
    operator fun <E : Element> contains(key: Key<E>): Boolean
    val isEmpty: Boolean
    val isNotEmpty: Boolean

    @Suppress("unused")
    interface Key<E : Element>

    interface Element {
        val key: Key<*>

        operator fun <E : Element> plus(element: E): List<Element> = listOf(this, element)
    }

    private class JsReaderContextInstance(private val elements: Map<Key<*>, Element>) : JsReaderContext {

        override fun <E : Element> plus(element: E): JsReaderContext =
            JsReaderContextInstance(this.elements + (element.key to element))

        override operator fun <E : Element> get(key: Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            elements[key]?.let { it as E }

        override operator fun <E : Element> contains(key: Key<E>): Boolean = elements.contains(key)

        override val isEmpty: Boolean
            get() = false
        override val isNotEmpty: Boolean
            get() = true
    }

    private object Empty : JsReaderContext {
        override fun <E : Element> plus(element: E): JsReaderContext = JsReaderContext(element)
        override fun <E : Element> get(key: Key<E>): E? = null
        override fun <E : Element> contains(key: Key<E>): Boolean = false

        override val isEmpty: Boolean
            get() = true
        override val isNotEmpty: Boolean
            get() = false
    }

    companion object {
        operator fun invoke(): JsReaderContext = Empty
        operator fun invoke(element: Element): JsReaderContext = JsReaderContextInstance(mapOf(element.key to element))
        operator fun invoke(elements: Collection<Element>): JsReaderContext =
            JsReaderContextInstance(elements.associateBy { it.key })
    }
}
