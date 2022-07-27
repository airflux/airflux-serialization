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

package io.github.airflux.dsl.reader.context.exception

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ExceptionHandlersTest : FreeSpec() {

    companion object {
        private val handlerOfSpecificException: ExceptionHandler = { _: JsReaderContext, _: JsLocation, _: Throwable ->
            JsonErrors.PathMissing
        }

        private val handlerOfGenericException: ExceptionHandler = { _: JsReaderContext, _: JsLocation, _: Throwable ->
            JsonErrors.PathMissing
        }
    }

    init {

        "ExceptionHandlers#get" - {
            val exception = IllegalArgumentException()

            "when the most specific exception is first" - {
                val handlers = ExceptionHandlers(
                    listOf(
                        IllegalArgumentException::class to handlerOfSpecificException,
                        Exception::class to handlerOfGenericException
                    )
                )

                "should return the handler for the most specific an exception" {
                    val handler = handlers[exception]
                    handler shouldBe handlerOfSpecificException
                }
            }

            "when the most specific exception is not first" - {
                val handlers = ExceptionHandlers(
                    listOf(
                        Exception::class to handlerOfGenericException,
                        IllegalArgumentException::class to handlerOfSpecificException
                    )
                )

                "should return the handler for the most generic an exception" {
                    val handler = handlers[exception]
                    handler shouldBe handlerOfGenericException
                }
            }

            "when no the exception handlers" - {
                val handlers = ExceptionHandlers(emptyList())

                "should return the null value" {
                    val handler = handlers[exception]
                    handler.shouldBeNull()
                }
            }
        }
    }
}
