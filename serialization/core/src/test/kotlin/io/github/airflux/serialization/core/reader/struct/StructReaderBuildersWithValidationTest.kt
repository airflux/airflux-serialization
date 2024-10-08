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

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.core.common.JsonErrors
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
import io.github.airflux.serialization.core.reader.struct.property.JsStructProperty
import io.github.airflux.serialization.core.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.core.reader.struct.property.startKeysOfPaths
import io.github.airflux.serialization.core.reader.struct.validation.JsStructValidator
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.cause
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructReaderBuildersWithValidationTest : FreeSpec() {

    init {

        "The buildStructReader function with validation" - {

            "when the reader was created" - {
                val reader = buildStructReader(
                    properties = PROPERTIES,
                    validator = VALIDATOR
                ) { _, _ ->
                    DTO(id = +ID_PROPERTY, name = +NAME_PROPERTY).toSuccess(LOCATION)
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "when the source does not have any errors" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                        )

                        "then the reader should return the DTO" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeSuccess()
                            result.location shouldBe LOCATION
                            result.value shouldBe DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                        }
                    }

                    "when the source is not a struct type" - {
                        val source: JsValue = JsString("")

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRUCT,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }

                    "when the source has a validation error" - {
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

                    "when the source has a read error" - {
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

                    "when the source has a validation and a read errors" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                    "when the source does not have any errors" - {
                        val source = JsStruct(
                            ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                        )

                        "then the reader should return the DTO" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeSuccess()
                            result.location shouldBe LOCATION
                            result.value shouldBe DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                        }
                    }

                    "when the source is not a struct type" - {
                        val source: JsValue = JsString("")

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRUCT,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }

                    "when the source has a validation error" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE)
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when the source has a read error" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }

                    "when the source has a validation and a read errors" - {
                        val source: JsValue = JsStruct(
                            ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE.toString()),
                            NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to JsBoolean.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it errors" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
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
                }
            }

            "when the type builder throw some exception" - {
                val reader: JsStructReader<EB, OPTS, DTO> =
                    buildStructReader(properties = PROPERTIES) { _, _ ->
                        throw InternalException()
                    }

                val source = JsStruct(
                    ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE.toString())!!,
                    NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<InternalException> {
                            reader.read(envWithFailFastIsTrue, LOCATION, source)
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<InternalException> {
                            reader.read(envWithFailFastIsFalse, LOCATION, source)
                        }
                    }
                }
            }
        }
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

        private val ID_PROPERTY = property(ID_PROPERTY_NAME, IntReader)
        private val NAME_PROPERTY = property(NAME_PROPERTY_NAME, StringReader)
        private val PROPERTIES = listOf(ID_PROPERTY, NAME_PROPERTY)

        private val VALIDATOR = JsStructValidator<EB, OPTS> { _, location, _, node ->
            node.forEach { (name, _) ->
                if (name !in PROPERTIES.startKeysOfPaths())
                    return@JsStructValidator invalid(
                        location = location.append(name),
                        error = JsonErrors.Validation.Struct.AdditionalProperties
                    )
            }
            valid()
        }

        private fun <EB, O, P> property(name: String, reader: JsReader<EB, O, P>): JsStructProperty<EB, O, P>
            where EB : InvalidTypeErrorBuilder,
                  EB : PathMissingErrorBuilder {
            val path = JsPath(name)
            val spec = StructPropertySpec(paths = JsPaths(path), reader = path.readRequired(reader))
            return JsStructProperty(spec)
        }
    }

    private class InternalException : RuntimeException()

    private data class DTO(val id: Int, val name: String?)

    private class EB : InvalidTypeErrorBuilder,
                       PathMissingErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption
}
