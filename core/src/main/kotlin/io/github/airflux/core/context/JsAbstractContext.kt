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

package io.github.airflux.core.context

public abstract class JsAbstractContext : JsContext {

    protected abstract val elements: Map<JsContext.Key<*>, JsContext.Element>

    override val isEmpty: Boolean
        get() = elements.isEmpty()

    @Suppress("UNCHECKED_CAST")
    override fun <E : JsContext.Element> getOrNull(key: JsContext.Key<E>): E? = elements[key]?.let { it as E }

    override operator fun <E : JsContext.Element> contains(key: JsContext.Key<E>): Boolean = elements.contains(key)

    protected fun <E : JsContext.Element> add(element: E): Map<JsContext.Key<*>, JsContext.Element> =
        elements + (element.key to element)

    protected fun <E : JsContext.Element> add(elements: Iterable<E>): Map<JsContext.Key<*>, JsContext.Element> =
        this.elements + elements.map { it.key to it }
}
