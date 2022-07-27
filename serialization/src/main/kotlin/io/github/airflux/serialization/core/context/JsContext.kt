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

package io.github.airflux.serialization.core.context

public interface JsContext {

    public val isEmpty: Boolean

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public fun <E : Element> getOrNull(key: Key<E>): E?

    public operator fun <E : Element> contains(key: Key<E>): Boolean

    public interface Key<E : Element>

    public interface Element {
        public val key: Key<*>
        public operator fun <E : Element> plus(element: E): List<Element> = listOf(this, element)
    }
}
