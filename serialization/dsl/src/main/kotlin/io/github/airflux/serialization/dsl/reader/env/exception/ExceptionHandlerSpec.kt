/*
 * Copyright 2021-2023 Maxim Sambulat.
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
import io.github.airflux.serialization.core.reader.result.ReadingResult
import kotlin.reflect.KClass

internal class ExceptionHandlerSpec<EB, O> private constructor(
    val exception: KClass<*>,
    val handler: (ReaderEnv<EB, O>, Location, Throwable) -> ReadingResult.Error
) {

    companion object {

        operator fun <EB, O, E : Throwable> invoke(
            exception: KClass<E>,
            handler: (ReaderEnv<EB, O>, Location, E) -> ReadingResult.Error
        ): ExceptionHandlerSpec<EB, O> =
            @Suppress("UNCHECKED_CAST")
            ExceptionHandlerSpec(
                exception = exception,
                handler = handler as (ReaderEnv<EB, O>, Location, Throwable) -> ReadingResult.Error
            )
    }
}
