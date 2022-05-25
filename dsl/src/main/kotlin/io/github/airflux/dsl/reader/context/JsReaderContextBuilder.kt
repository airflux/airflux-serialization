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

package io.github.airflux.dsl.reader.context

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.ErrorBuilder
import io.github.airflux.core.reader.context.option.FailFast
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.context.exception.ExceptionsHandlerBuilder

fun readerContext(block: JsReaderContextBuilder.() -> Unit): JsReaderContext =
    JsReaderContextBuilder().apply(block).build()

@AirfluxMarker
class JsReaderContextBuilder {

    private val elements = mutableListOf<JsReaderContext.Element>()

    var failFast: Boolean? = null

    fun errorBuilders(block: ErrorsBuilder.() -> Unit) {
        val errors = ErrorsBuilder().apply(block).build()
        elements.addAll(errors)
    }

    fun exceptions(block: ExceptionsHandlerBuilder.() -> Unit) {
        ExceptionsHandlerBuilder().apply(block).build().also { elements.add(it) }
    }

    internal fun build(): JsReaderContext {
        failFast?.let { isTrue ->
            FailFast(isTrue = isTrue).also { elements.add(it) }
        }

        return JsReaderContext(elements)
    }

    @AirfluxMarker
    class ErrorsBuilder {
        private val items = mutableListOf<JsReaderContext.Element>()

        fun <E> register(error: E)
            where E : ErrorBuilder,
                  E : JsReaderContext.Element {
            items.add(error)
        }

        operator fun <E> E.unaryPlus()
            where E : ErrorBuilder,
                  E : JsReaderContext.Element = register(this)

        internal fun build(): List<JsReaderContext.Element> = items
    }
}
