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

import io.github.airflux.serialization.core.context.Context
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult

public fun exceptionsHandler(vararg handlers: ExceptionHandlerSpec): ExceptionsHandler {
    val handlersContainer = ExceptionHandlersContainer(handlers.toList())
    return ExceptionsHandler { context, location, exception ->
        val handler = handlersContainer[exception] ?: throw exception
        handler(context, location, exception)
    }
}

public class ExceptionsHandler(
    private val handler: (ReaderContext, Location, Throwable) -> ReaderResult.Error
) : Context.Element {

    public fun handle(context: ReaderContext, location: Location, exception: Throwable): ReaderResult.Error =
        handler(context, location, exception)

    override val key: Context.Key<*> = Key

    internal companion object Key : Context.Key<ExceptionsHandler>
}
