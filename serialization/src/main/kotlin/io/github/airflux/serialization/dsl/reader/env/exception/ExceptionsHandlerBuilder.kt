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

package io.github.airflux.serialization.dsl.reader.env.exception

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.exception.ExceptionHandlerSpec
import io.github.airflux.serialization.core.reader.env.exception.ExceptionHandlersContainer
import io.github.airflux.serialization.core.reader.env.exception.ExceptionsHandler
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.dsl.AirfluxMarker
import kotlin.reflect.KClass

/**
 * Creates a [ExceptionsHandler] instance.
 */
public fun <EB, CTX> exceptionsHandler(block: ExceptionsHandlerBuilder<EB, CTX>.() -> Unit): ExceptionsHandler<EB, CTX> =
    ExceptionsHandlerBuilder<EB, CTX>().apply(block).build()

/**
 * The handle **uncaught** exceptions.
 */
@AirfluxMarker
public class ExceptionsHandlerBuilder<EB, CTX> {

    private val handlerSpecs: MutableList<ExceptionHandlerSpec<EB, CTX>> = mutableListOf()

    public inline fun <reified E : Throwable> exception(
        noinline handler: (ReaderEnv<EB, CTX>, Location, E) -> ReaderResult.Error
    ): Unit = exception(E::class, handler)

    public fun <E : Throwable> exception(
        exception: KClass<E>,
        handler: (ReaderEnv<EB, CTX>, Location, E) -> ReaderResult.Error
    ) {
        handlerSpecs.add(ExceptionHandlerSpec(exception = exception, handler = handler))
    }

    internal fun build(): ExceptionsHandler<EB, CTX> {
        val handlersContainer: ExceptionHandlersContainer<EB, CTX> = ExceptionHandlersContainer(handlerSpecs)
        return ExceptionsHandler { env, location, exception ->
            val handler = handlersContainer[exception] ?: throw exception
            handler(env, location, exception)
        }
    }
}
