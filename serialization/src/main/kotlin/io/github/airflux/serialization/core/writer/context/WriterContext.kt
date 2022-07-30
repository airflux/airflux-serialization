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

package io.github.airflux.serialization.core.writer.context

import io.github.airflux.serialization.core.context.AbstractContext
import io.github.airflux.serialization.core.context.Context

public class WriterContext private constructor(
    override val elements: Map<Context.Key<*>, Context.Element>
) : AbstractContext() {

    public operator fun <E : Context.Element> plus(element: E): WriterContext = WriterContext(add(element))
    public operator fun <E : Context.Element> plus(elements: Iterable<E>): WriterContext =
        WriterContext(add(elements))

    internal companion object {

        operator fun invoke(): WriterContext = WriterContext(emptyMap())

        operator fun <E : Context.Element> invoke(element: E): WriterContext =
            WriterContext(mapOf(element.key to element))

        operator fun <E : Context.Element> invoke(elements: Iterable<E>): WriterContext =
            WriterContext(elements.associateBy { it.key })
    }
}
