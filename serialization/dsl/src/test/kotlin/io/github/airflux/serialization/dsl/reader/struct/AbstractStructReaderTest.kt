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

package io.github.airflux.serialization.dsl.reader.struct

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class AbstractStructReaderTest : FreeSpec() {

    init {

        "The AbstractStructReader type" - {

            "when the reader was created" - {
                val reader = Reader()

                "when the source is the struct type" - {
                    val source: JsValue = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                    "then the reader should return a value" {
                        val result = reader.read(ENV, LOCATION, source)
                        result shouldBeSuccess success(location = LOCATION, value = ID_PROPERTY_VALUE)
                    }
                }

                "when the source is not the struct type" - {
                    val source: JsValue = JsString("")

                    "then the reader should return an error" {
                        val result = reader.read(ENV, LOCATION, source)
                        result shouldBeFailure failure(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.STRUCT,
                                actual = JsValue.Type.STRING
                            )
                        )
                    }
                }
            }
        }
    }

    private class Reader : AbstractStructReader<EB, Unit, String>() {
        override val properties: StructProperties<EB, Unit>
            get() = emptyList()

        override fun read(env: JsReaderEnv<EB, Unit>, location: JsLocation, source: JsStruct): JsReaderResult<String> =
            JsReaderResult.Success(location = location, value = ID_PROPERTY_VALUE)
    }

    private companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "42"

        private val LOCATION: JsLocation = JsLocation
        private val ENV = JsReaderEnv(EB(), Unit)
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
