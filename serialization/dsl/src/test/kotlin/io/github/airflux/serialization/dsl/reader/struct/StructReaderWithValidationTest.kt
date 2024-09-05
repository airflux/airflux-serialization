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
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.readRequired
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.common.DummyStructValidator
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.kotest.assertions.cause
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructReaderWithValidationTest : FreeSpec() {

    init {

        "The StructReaderWithValidation type" - {

            "when the reader was created" - {
                val validator = DummyStructValidator.additionalProperties<EB, OPTS>(
                    nameProperties = setOf(ID_PROPERTY_NAME, NAME_PROPERTY_NAME),
                    error = JsonErrors.Validation.Struct.AdditionalProperties
                )

                val reader = StructReaderWithValidation(validator, reader())

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "when the error occurs only at the validation step" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when the error occurs only in the read step" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_FALSE

                    "when the error occurs only at the validation step" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when the error occurs only in the read step" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }

                    "when the error occurs at the validation step and in the read step" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it errors" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                ),
                                cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.NUMBER,
                                        actual = JsValue.Type.STRING
                                    )
                                )
                            )
                        }
                    }

                    "when no validation or reading errors occur" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
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

    private fun reader(): JsStructReader<EB, OPTS, DTO> {
        val idProperty = property(ID_PROPERTY_NAME, IntReader)
        val nameProperty = property(NAME_PROPERTY_NAME, StringReader)
        val properties = listOf(idProperty, nameProperty)
        return PropertiesStructReader(
            properties = properties,
            typeBuilder = { _, _ ->
                DTO(id = +idProperty, name = +nameProperty).toSuccess(LOCATION)
            }
        )
    }

    private fun <EB, O, P> property(name: String, reader: JsReader<EB, O, P>): StructProperty<EB, O, P>
        where EB : InvalidTypeErrorBuilder,
              EB : PathMissingErrorBuilder {
        val path = JsPath(name)
        val spec = StructPropertySpec(paths = JsPaths(path), reader = path.readRequired(reader))
        return StructProperty(spec)
    }

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"
        private const val IS_ACTIVE_PROPERTY_NAME = "isActive"
        private const val IS_ACTIVE_PROPERTY_VALUE = true

        private val ENV_WITH_FAIL_FAST_IS_TRUE =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))
        private val ENV_WITH_FAIL_FAST_IS_FALSE =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = false)))

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
        private val IntReader: JsReader<EB, OPTS, Int> = DummyReader.int()
    }

    private data class DTO(val id: Int, val name: String?)

    private class EB : InvalidTypeErrorBuilder,
                       PathMissingErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption
}
