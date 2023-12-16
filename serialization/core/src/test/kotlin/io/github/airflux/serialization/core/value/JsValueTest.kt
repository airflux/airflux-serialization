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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.NonEmptyList
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsValueTest : FreeSpec() {

    companion object {
        private const val VALUE = "value"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private val READER: JsReader<EB, Unit, String> = DummyReader.string()
    }

    init {

        "The JsValue type" - {

            "when calling the `read()` function" - {

                "when the expected type equals the actual type" - {
                    val value: JsValue = JsString(VALUE)

                    "then the function should return a successful result" {
                        val result = value.read(ENV, LOCATION, READER)
                        result.shouldBeSuccess().value shouldBe VALUE
                    }
                }

                "when the value contains the finding key" - {
                    val value: JsValue = JsBoolean.True

                    "then the function should return the error" {
                        val result = value.read(ENV, LOCATION, READER)
                        result shouldBeFailure JsReaderResult.Failure(
                            NonEmptyList(
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(listOf(JsString.nameOfType), JsBoolean.nameOfType)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {

        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
