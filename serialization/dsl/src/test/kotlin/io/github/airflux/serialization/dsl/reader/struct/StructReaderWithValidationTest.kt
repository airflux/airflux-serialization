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
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.dsl.common.DummyStructValidatorBuilder
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.reader.struct.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructReaderWithValidationTest : FreeSpec() {

    companion object {

        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"
        private const val IS_ACTIVE_PROPERTY_NAME = "isActive"
        private const val IS_ACTIVE_PROPERTY_VALUE = true

        private val ENV_WITH_FAIL_FAST_IS_TRUE = JsReaderEnv(EB(), OPTS(failFast = true))
        private val ENV_WITH_FAIL_FAST_IS_FALSE = JsReaderEnv(EB(), OPTS(failFast = false))

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
        private val IntReader: JsReader<EB, OPTS, Int> = DummyReader.int()
    }

    init {

        "The StructReaderWithValidation type" - {

            "when was created reader" - {
                val validator = DummyStructValidatorBuilder.additionalProperties<EB, OPTS>(
                    nameProperties = setOf(ID_PROPERTY_NAME, NAME_PROPERTY_NAME),
                    error = JsonErrors.Validation.Struct.AdditionalProperties
                )
                val reader: JsStructReader<EB, OPTS, DTO> = structReader {
                    validation(validator)
                    val id = property(required(name = ID_PROPERTY_NAME, reader = IntReader))
                    val name = property(optional(name = NAME_PROPERTY_NAME, reader = StringReader))
                    returns { _, location ->
                        DTO(id = +id, name = +name).toSuccess(location)
                    }
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "when the error occurs only at the validation step" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when the error occurs only in the read step" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsNumeric.Integer.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_FALSE

                    "when the error occurs only at the validation step" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when the error occurs only in the read step" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsNumeric.Integer.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }

                    "when the error occurs at the validation step and in the read step" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it errors" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                ),
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsNumeric.Integer.nameOfType),
                                        actual = JsString.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when no validation or reading errors occur" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                        )

                        "then the reader should return an instance of the type" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            val success = result.shouldBeSuccess()
                            success.value shouldBe DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                        }
                    }
                }
            }
        }
    }

    internal data class DTO(val id: Int, val name: String?)

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
