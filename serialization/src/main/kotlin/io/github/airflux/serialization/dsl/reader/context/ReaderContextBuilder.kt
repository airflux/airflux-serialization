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

import io.github.airflux.serialization.core.context.Context
import io.github.airflux.serialization.core.context.error.ContextErrorBuilderElement
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandlerBuilder

public fun readerContext(block: ReaderContextBuilder.() -> Unit = {}): ReaderContext =
    ReaderContextBuilder().apply(block).build()

@AirfluxMarker
public class ReaderContextBuilder internal constructor() {

    private val elements = mutableListOf<Context.Element>()

    public var failFast: Boolean? = null

    public fun errorBuilders(block: ErrorsBuilder.() -> Unit) {
        val errors = ErrorsBuilder().apply(block).build()
        elements.addAll(errors)
    }

    public fun exceptions(block: ExceptionsHandlerBuilder.() -> Unit) {
        ExceptionsHandlerBuilder().apply(block).build().also { elements.add(it) }
    }

    internal fun build(): ReaderContext {
        failFast?.let { isTrue ->
            FailFast(value = isTrue).also { elements.add(it) }
        }

        return ReaderContext(elements)
    }

    @AirfluxMarker
    public class ErrorsBuilder internal constructor() {
        private val items = mutableListOf<ContextErrorBuilderElement>()

        public fun <E : ContextErrorBuilderElement> register(error: E) {
            items.add(error)
        }

        public operator fun <E : ContextErrorBuilderElement> E.unaryPlus(): Unit = register(this)

        internal fun build(): List<Context.Element> = items
    }
}
