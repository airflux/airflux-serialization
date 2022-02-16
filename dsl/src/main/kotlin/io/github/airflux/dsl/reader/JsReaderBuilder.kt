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

package io.github.airflux.dsl.reader

import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.dsl.common.instanceOf
import kotlin.reflect.KClass

internal typealias ExceptionHandler = (JsLocation, Throwable) -> JsResult.Failure

interface JsReaderBuilder {

    interface Options {
        val failFast: Boolean
    }

    class Configuration private constructor(
        override val failFast: Boolean,
        val errorBuilders: ErrorBuilders,
        val exceptionHandlers: ExceptionHandlers
    ) : Options {

        data class ErrorBuilders(
            val pathMissing: PathMissingErrorBuilder,
            val invalidType: InvalidTypeErrorBuilder
        )

        class ExceptionHandlers(private val exceptions: List<Pair<KClass<*>, ExceptionHandler>>) {
            operator fun get(exception: Throwable): ExceptionHandler? =
                exceptions.find { exception.instanceOf(it.first) }?.second
        }

        class Builder(
            val pathMissingErrorBuilder: PathMissingErrorBuilder,
            val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ) {
            private val exceptions = mutableListOf<Pair<KClass<*>, ExceptionHandler>>()

            var failFast: Boolean = true

            inline fun <reified E> exception(noinline handler: (JsLocation, E) -> JsResult.Failure)
                where E : Throwable {
                exception(E::class, handler)
            }

            fun <E : Throwable> exception(klass: KClass<E>, handler: (JsLocation, E) -> JsResult.Failure) {
                @Suppress("UNCHECKED_CAST")
                exceptions += klass to handler as (JsLocation, Throwable) -> JsResult.Failure
            }

            internal fun build() = Configuration(
                failFast = failFast,
                errorBuilders = ErrorBuilders(
                    pathMissing = pathMissingErrorBuilder,
                    invalidType = invalidTypeErrorBuilder
                ),
                exceptionHandlers = ExceptionHandlers(exceptions),
            )
        }

        companion object {

            fun build(
                pathMissingErrorBuilder: PathMissingErrorBuilder,
                invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
                block: Builder.() -> Unit
            ): Configuration = Builder(pathMissingErrorBuilder, invalidTypeErrorBuilder).apply(block).build()
        }
    }
}
