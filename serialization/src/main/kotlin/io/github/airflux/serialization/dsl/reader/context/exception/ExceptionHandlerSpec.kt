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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import kotlin.reflect.KClass

public inline fun <reified E : Throwable> exception(
    noinline handler: (ReaderContext, Location, E) -> ReaderResult.Error
): ExceptionHandlerSpec =
    exception(E::class, handler)

public fun <E : Throwable> exception(
    kClass: KClass<E>,
    handler: (ReaderContext, Location, E) -> ReaderResult.Error
): ExceptionHandlerSpec =
    ExceptionHandlerSpec(kClass, handler)

public class ExceptionHandlerSpec private constructor(
    internal val exception: KClass<*>,
    internal val handler: (ReaderContext, Location, Throwable) -> ReaderResult.Error
) {

    internal companion object {

        operator fun <E : Throwable> invoke(
            exception: KClass<E>,
            handler: (ReaderContext, Location, E) -> ReaderResult.Error
        ): ExceptionHandlerSpec =
            @Suppress("UNCHECKED_CAST")
            ExceptionHandlerSpec(
                exception = exception,
                handler = handler as (ReaderContext, Location, Throwable) -> ReaderResult.Error
            )
    }
}
