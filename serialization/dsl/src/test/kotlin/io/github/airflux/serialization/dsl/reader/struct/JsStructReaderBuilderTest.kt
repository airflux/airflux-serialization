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
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.common.DummyStructValidator
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.property.startKeysOfPaths
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec

internal class JsStructReaderBuilderTest : FreeSpec() {

    init {

        "The JsStructReaderBuilder type" - {

            "when the reader was created without any validations" - {
                val reader: JsStructReader<EB, OPTS, DTO> = structReader {
                    val id = property(required(name = ID_PROPERTY_NAME, reader = StringReader))
                    val name = property(required(name = NAME_PROPERTY_NAME, reader = StringReader))
                    returns { _, location ->
                        DTO(id = +id, name = +name).toSuccess(location)
                    }
                }

                "then the reader should return the DTO instance" {
                    val result = reader.read(ENV, LOCATION, source)

                    result.shouldBeSuccess(
                        location = LOCATION,
                        value = DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                    )
                }
            }

            "when the reader was created with some validation" - {

                "when the validator instance was passed" - {
                    val validator = DummyStructValidator.additionalProperties<EB, OPTS>(
                        nameProperties = setOf(ID_PROPERTY_NAME, NAME_PROPERTY_NAME),
                        error = JsonErrors.Validation.Struct.AdditionalProperties
                    )

                    val reader: JsStructReader<EB, OPTS, DTO> = structReader {
                        validation(validator)
                        val id = property(required(name = ID_PROPERTY_NAME, reader = StringReader))
                        val name = property(required(name = NAME_PROPERTY_NAME, reader = StringReader))
                        returns { _, location ->
                            DTO(id = +id, name = +name).toSuccess(location)
                        }
                    }

                    "then the reader should return the validation error" {
                        val result = reader.read(ENV, LOCATION, source)

                        result.shouldBeFailure(
                            location = LOCATION.append(TITLE_PROPERTY_NAME),
                            error = JsonErrors.Validation.Struct.AdditionalProperties
                        )
                    }
                }

                "when the validator builder instance was passed" - {
                    val reader: JsStructReader<EB, OPTS, DTO> = structReader {
                        validation { properties ->
                            DummyStructValidator.additionalProperties(
                                nameProperties = properties.startKeysOfPaths(),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                        val id = property(required(name = ID_PROPERTY_NAME, reader = StringReader))
                        val name = property(required(name = NAME_PROPERTY_NAME, reader = StringReader))
                        returns { _, location ->
                            DTO(id = +id, name = +name).toSuccess(location)
                        }
                    }

                    "then the reader should return the validation error" {
                        val result = reader.read(ENV, LOCATION, source)

                        result.shouldBeFailure(
                            location = LOCATION.append(TITLE_PROPERTY_NAME),
                            error = JsonErrors.Validation.Struct.AdditionalProperties
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "42"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "Mr."

        private val ENV = JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))
        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()

        private val source = JsStruct(
            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
            TITLE_PROPERTY_NAME to JsString(TITLE_PROPERTY_VALUE)
        )
    }

    private data class DTO(val id: String, val name: String)

    private class EB : InvalidTypeErrorBuilder,
                       PathMissingErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption
}
