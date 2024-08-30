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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class JsArrayReaderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private val reader: JsReader<EB, Unit, JsArray> = JsArrayReader.build()

        private const val TEXT = "abc"
        private const val FIRST_ITEM = "123"
        private const val SECOND_ITEM = "456"
    }

    init {

        "The JsArray type reader" - {

            "when the source is JsArray" - {
                val source: JsValue = JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))

                "then reader should return the source value" {
                    val result = reader.read(ENV, LOCATION, source)

                    result.shouldBeSuccess()
                    result.value.shouldBeSameInstanceAs(source)
                }
            }

            "when the source is not JsArray" - {
                val source: JsValue = JsString(TEXT)

                "then the reader should return the invalid type error" {
                    val result = reader.read(ENV, LOCATION, source)

                    result.shouldBeFailure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = JsValue.Type.ARRAY,
                            actual = JsValue.Type.STRING
                        )
                    )
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
