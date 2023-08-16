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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

internal class ExceptionsHandlerBuilderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(Unit, Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {
        "The ExceptionsHandler" - {
            val handler = exceptionsHandler<Unit, Unit> {
                exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
            }

            "when trying to handle an exception that has a handler" - {
                val exception = IllegalArgumentException()

                "then should return a result of handling" {
                    val result = handler.handle(ENV, LOCATION, exception)
                    result shouldBe JsonErrors.PathMissing
                }
            }

            "when trying to handle an exception that has not a handler" - {
                val exception = IllegalStateException()

                "then should re-throwing the exception" {
                    assertThrows<IllegalStateException> {
                        handler.handle(ENV, LOCATION, exception)
                    }
                }
            }
        }
    }
}
