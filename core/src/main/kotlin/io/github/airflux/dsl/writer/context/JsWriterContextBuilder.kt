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

package io.github.airflux.dsl.writer.context

import io.github.airflux.core.context.JsContext
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.core.writer.context.option.WriteActionIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty
import io.github.airflux.dsl.AirfluxMarker

public fun writerContext(block: JsWriterContextBuilder.() -> Unit): JsWriterContext =
    JsWriterContextBuilder().apply(block).build()

@AirfluxMarker
public class JsWriterContextBuilder internal constructor() {

    private val elements = mutableListOf<JsContext.Element>()

    public var writeActionIfArrayIsEmpty: WriteActionIfArrayIsEmpty.Action? = null
    public var writeActionIfObjectIsEmpty: WriteActionIfObjectIsEmpty.Action? = null

    internal fun build(): JsWriterContext {
        writeActionIfArrayIsEmpty?.let { action ->
            WriteActionIfArrayIsEmpty(value = action).also { elements.add(it) }
        }

        writeActionIfObjectIsEmpty?.let { action ->
            WriteActionIfObjectIsEmpty(value = action).also { elements.add(it) }
        }

        return JsWriterContext(elements)
    }
}
