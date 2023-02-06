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

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ExceptionHandlersContainerTest : FreeSpec() {

    companion object {
        private val specificExceptionHandler =
            { _: ReaderEnv<Unit, Unit>, _: Location, _: IllegalArgumentException -> JsonErrors.PathMissing }

        private val genericExceptionHandler =
            { _: ReaderEnv<Unit, Unit>, _: Location, _: Exception -> JsonErrors.PathMissing }
    }

    init {

        "ExceptionHandlersContainer#get" - {
            val exception = IllegalArgumentException()

            "when the most specific exception is first" - {
                val handlers = ExceptionHandlersContainer(
                    listOf(
                        exceptionHandlerSpec(specificExceptionHandler),
                        exceptionHandlerSpec(genericExceptionHandler)
                    )
                )

                "should return the handler for the most specific an exception" {
                    val handler = handlers[exception]
                    handler shouldBe specificExceptionHandler
                }
            }

            "when the most specific exception is not first" - {
                val handlers = ExceptionHandlersContainer(
                    listOf(
                        exceptionHandlerSpec(genericExceptionHandler),
                        exceptionHandlerSpec(specificExceptionHandler)
                    )
                )

                "should return the handler for the most generic an exception" {
                    val handler = handlers[exception]
                    handler shouldBe genericExceptionHandler
                }
            }

            "when no the exception handlers" - {
                val handlers = ExceptionHandlersContainer<Unit, Unit>(emptyList())

                "should return the null value" {
                    val handler = handlers[exception]
                    handler.shouldBeNull()
                }
            }
        }
    }

    private inline fun <EB, O, reified E : Throwable> exceptionHandlerSpec(
        noinline handler: (ReaderEnv<EB, O>, Location, E) -> ReaderResult.Error
    ): ExceptionHandlerSpec<EB, O> =
        ExceptionHandlerSpec(E::class, handler)
}
