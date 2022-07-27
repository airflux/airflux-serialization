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

package io.github.airflux.serialization.dsl.reader.context

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandler
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsReaderContextBuilderTest : FreeSpec() {

    init {

        "when the context is not initialized" - {
            val context: JsReaderContext = readerContext()

            "then context is empty" {
                context.isEmpty shouldBe true
            }
        }

        "when any parameters are not set in the builder" - {
            val context: JsReaderContext = readerContext {}

            "then the context is empty" {
                context.isEmpty shouldBe true
            }
        }

        "when failFast parameter is set in the builder" - {
            val context: JsReaderContext = readerContext {
                failFast = false
            }

            "then the context should contain the FailFast option" {
                context.contains(FailFast) shouldBe true
            }
        }

        "when the error builder was registered" - {
            val context: JsReaderContext = readerContext {
                errorBuilders {
                    +PathMissingErrorBuilder { JsonErrors.PathMissing }
                }
            }

            "then the context should contain registered the error builder" {
                context.contains(PathMissingErrorBuilder) shouldBe true
            }
        }

        "when the exception handler was registered" - {
            val context: JsReaderContext = readerContext {
                exceptions {
                    exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
                }
            }

            "then the context should contain registered the exception handler" {
                context.contains(ExceptionsHandler) shouldBe true
            }
        }
    }
}
