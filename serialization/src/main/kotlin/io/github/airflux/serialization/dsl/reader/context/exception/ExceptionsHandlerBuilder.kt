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

package io.github.airflux.serialization.dsl.reader.context.exception

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.dsl.AirfluxMarker
import kotlin.reflect.KClass

@AirfluxMarker
public class ExceptionsHandlerBuilder internal constructor() {

    private val handlers = mutableListOf<Pair<KClass<*>, ExceptionHandler>>()

    @Suppress("unused")
    public inline fun <reified E : Throwable> exception(
        noinline handler: (JsReaderContext, JsLocation, E) -> JsError
    ): Unit =
        exception(E::class, handler)

    public fun <E : Throwable> exception(kClass: KClass<E>, handler: (JsReaderContext, JsLocation, E) -> JsError) {
        @Suppress("UNCHECKED_CAST")
        handlers += kClass to handler as (JsReaderContext, JsLocation, Throwable) -> JsError
    }

    internal fun build(): ExceptionsHandler {
        val handlers = ExceptionHandlers(handlers)
        return ExceptionsHandler { context, location, exception ->
            val handler: ExceptionHandler = handlers[exception] ?: throw exception
            handler.invoke(context, location, exception)
        }
    }
}