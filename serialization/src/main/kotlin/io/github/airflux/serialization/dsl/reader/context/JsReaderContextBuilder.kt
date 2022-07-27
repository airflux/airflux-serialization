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

package io.github.airflux.serialization.dsl.reader.context

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.context.error.JsContextErrorBuilderElement
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandlerBuilder

public fun readerContext(block: JsReaderContextBuilder.() -> Unit): JsReaderContext =
    JsReaderContextBuilder().apply(block).build()

@io.github.airflux.serialization.dsl.AirfluxMarker
public class JsReaderContextBuilder internal constructor() {

    private val elements = mutableListOf<JsContext.Element>()

    public var failFast: Boolean? = null

    public fun errorBuilders(block: ErrorsBuilder.() -> Unit) {
        val errors = ErrorsBuilder().apply(block).build()
        elements.addAll(errors)
    }

    public fun exceptions(block: ExceptionsHandlerBuilder.() -> Unit) {
        ExceptionsHandlerBuilder().apply(block).build().also { elements.add(it) }
    }

    internal fun build(): JsReaderContext {
        failFast?.let { isTrue ->
            FailFast(value = isTrue).also { elements.add(it) }
        }

        return JsReaderContext(elements)
    }

    @io.github.airflux.serialization.dsl.AirfluxMarker
    public class ErrorsBuilder internal constructor() {
        private val items = mutableListOf<JsContextErrorBuilderElement>()

        public fun <E : JsContextErrorBuilderElement> register(error: E) {
            items.add(error)
        }

        public operator fun <E : JsContextErrorBuilderElement> E.unaryPlus(): Unit = register(this)

        internal fun build(): List<JsContext.Element> = items
    }
}
