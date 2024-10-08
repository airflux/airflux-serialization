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
import io.github.airflux.serialization.core.lookup.JsLookupResult
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec

internal class RequiredPropertyReaderTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "a64d62c7-4a57-4282-bce3-3cd52b815204"

        private val ENV = JsReaderEnv(config = JsReaderEnv.Config(EB(), Unit))
        private val LOCATION: JsLocation = JsLocation
        private val READER: JsReader<EB, Unit, String> = DummyReader.string()
    }

    init {

        "The readRequired function" - {

            "when the element is defined" - {
                val lookup: JsLookupResult = JsLookupResult.Defined(
                    location = LOCATION.append(ID_PROPERTY_NAME),
                    value = JsString(ID_PROPERTY_VALUE)
                )

                "then should return the result of applying the reader" {
                    val result: JsReaderResult<String?> = lookup.readRequired(env = ENV, using = READER)

                    result.shouldBeSuccess(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        value = ID_PROPERTY_VALUE
                    )
                }
            }

            "when the element is missing" - {
                val lookup: JsLookupResult =
                    JsLookupResult.Undefined.PathMissing(location = LOCATION.append(ID_PROPERTY_NAME))

                "then should return the missing path error" {
                    val result: JsReaderResult<String?> = lookup.readRequired(env = ENV, using = READER)

                    result.shouldBeFailure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.PathMissing
                    )
                }
            }

            "when the element is invalid type" - {
                val lookup: JsLookupResult = JsLookupResult.Undefined.InvalidType(
                    expected = JsValue.Type.STRUCT,
                    actual = JsValue.Type.STRING,
                    location = LOCATION.append(ID_PROPERTY_NAME)
                )

                "then should return the invalid type error" {
                    val result: JsReaderResult<String?> = lookup.readRequired(env = ENV, using = READER)

                    result.shouldBeFailure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.InvalidType(
                            expected = JsValue.Type.STRUCT,
                            actual = JsValue.Type.STRING
                        )
                    )
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
