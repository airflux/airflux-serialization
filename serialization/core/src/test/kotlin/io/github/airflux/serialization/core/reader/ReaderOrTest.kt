/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class ReaderOrTest : FreeSpec() {

    companion object {
        private const val LEFT_VALUE = "true"
        private const val RIGHT_VALUE = "false"
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {
        "The extension-function JsReader#or" - {

            "when left reader returns an value" - {
                val leftReader: JsReader<EB, Unit, String> =
                    DummyReader(success(location = LOCATION, value = LEFT_VALUE))
                val rightReader: JsReader<EB, Unit, String> =
                    DummyReader(success(location = LOCATION, value = RIGHT_VALUE))

                val reader = leftReader or rightReader

                "then the right reader doesn't execute" {
                    val result = reader.read(ENV, LOCATION, JsNull)
                    result shouldBeSuccess success(location = LOCATION, value = LEFT_VALUE)
                }
            }

            "when left reader returns an error" - {
                val leftReader: JsReader<EB, Unit, String> = DummyReader { _, _, _ ->
                    failure(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                }

                "when the right reader returns an value" - {
                    val rightReader: JsReader<EB, Unit, String> =
                        DummyReader(success(location = LOCATION, value = RIGHT_VALUE))

                    val reader = leftReader or rightReader

                    "then the result of the right reader should be returned" {
                        val result = reader.read(ENV, LOCATION, JsNull)
                        result shouldBeSuccess success(location = LOCATION, value = RIGHT_VALUE)
                    }
                }

                "when the right reader returns an error" - {
                    val rightReader: JsReader<EB, Unit, String> = DummyReader { _, _, _ ->
                        failure(
                            location = LOCATION.append("identifier"),
                            error = JsonErrors.InvalidType(
                                expected = listOf(JsValue.Type.STRING),
                                actual = JsValue.Type.NUMBER
                            )
                        )
                    }
                    val reader = leftReader or rightReader

                    "then both errors should be returned" {
                        val result = reader.read(ENV, LOCATION, JsNull)

                        result.shouldBeFailure(
                            JsReaderResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.PathMissing
                            ),
                            JsReaderResult.Failure.Cause(
                                location = LOCATION.append("identifier"),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsValue.Type.STRING),
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: Iterable<JsValue.Type>, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
