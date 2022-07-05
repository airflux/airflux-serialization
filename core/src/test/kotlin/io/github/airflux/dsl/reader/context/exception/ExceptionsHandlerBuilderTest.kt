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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ExceptionsHandlerBuilderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
        private val EXCEPTION = IllegalArgumentException()
    }

    init {

        "when an exception handler is not registered in the builder" - {
            val exceptionsHandler: ExceptionsHandler = ExceptionsHandlerBuilder().build()

            "then the handleException should re-throw an exception" {
                shouldThrow<IllegalArgumentException> {
                    exceptionsHandler.handleException(CONTEXT, LOCATION, EXCEPTION)
                }
            }
        }

        "when an exception handler is registered in the builder" - {
            val exceptionsHandler = ExceptionsHandlerBuilder()
                .apply {
                    exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
                }.build()

            "then the handleException should return the error" {
                val error = exceptionsHandler.handleException(CONTEXT, LOCATION, EXCEPTION)
                error shouldBe JsonErrors.PathMissing
            }
        }
    }
}
