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

import io.github.airflux.core.context.JsAbstractContext
import io.github.airflux.core.context.JsContext

public class JsReaderContext private constructor(
    override val elements: Map<JsContext.Key<*>, JsContext.Element>
) : JsAbstractContext() {

    public operator fun <E : JsContext.Element> plus(element: E): JsReaderContext = JsReaderContext(add(element))
    public operator fun <E : JsContext.Element> plus(elements: Iterable<E>): JsReaderContext =
        JsReaderContext(add(elements))

    internal companion object {

        operator fun invoke(): JsReaderContext = JsReaderContext(emptyMap())

        operator fun <E : JsContext.Element> invoke(element: E): JsReaderContext =
            JsReaderContext(mapOf(element.key to element))

        operator fun <E : JsContext.Element> invoke(elements: Iterable<E>): JsReaderContext =
            JsReaderContext(elements.associateBy { it.key })
    }
}
