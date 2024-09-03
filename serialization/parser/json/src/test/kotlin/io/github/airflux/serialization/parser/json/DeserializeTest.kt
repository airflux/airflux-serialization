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

package io.github.airflux.serialization.parser.json

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class DeserializeTest : FreeSpec() {

    init {

        "The `deserialize` function" - {

            "when JSON is empty" - {
                val json = ""

                "then the function should return a context missing error" {
                    val result: JsReaderResult<Boolean> = json.deserialize(ENV, reader)
                    result.shouldBeFailure(location = LOCATION, error = ContextMissingError)
                }
            }

            "when JSON contains valid boolean value" - {
                val json = "true"

                "then the function should return a successful result" {
                    val result: JsReaderResult<Boolean> = json.deserialize(ENV, reader)
                    result.shouldBeSuccess().value shouldBe true
                }
            }

            "when JSON is invalid" - {
                val json = "{"

                "then the function should return a parsing error" {
                    val result: JsReaderResult<Boolean> = json.deserialize(ENV, reader)
                    result.shouldBeFailure(location = LOCATION, error = ParsingError)
                }
            }
        }
    }

    companion object {
        private val ENV: JsReaderEnv<EB, Options> =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = Options))
        private val LOCATION: JsLocation = JsLocation.Root

        private val reader: JsReader<EB, Options, Boolean> = JsReader { env, location, value ->
            when (value) {
                is JsBoolean.True -> JsReaderResult.Success(location, true)
                is JsBoolean.False -> JsReaderResult.Success(location, false)
                else -> JsReaderResult.Failure(
                    location = location,
                    error = env.config.errorBuilders.invalidTypeError(
                        expected = JsValue.Type.BOOLEAN,
                        actual = value.type
                    )
                )
            }
        }
    }

    internal data object ParsingError : JsReaderResult.Error
    internal data object ContextMissingError : JsReaderResult.Error
    internal data class InvalidTypeError(
        val expected: JsValue.Type,
        val actual: JsValue.Type
    ) : JsReaderResult.Error

    internal class EB : ParsingErrorBuilder,
                        InvalidTypeErrorBuilder {
        override fun parsingError(description: String, position: Int, line: Int, column: Int): JsReaderResult.Error =
            ParsingError

        override fun contextMissingError(): JsReaderResult.Error = ContextMissingError

        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            InvalidTypeError(expected, actual)
    }

    internal object Options : PropertyNamePoolOption {
        override val usePropertyNamePool: Boolean = false
    }
}
