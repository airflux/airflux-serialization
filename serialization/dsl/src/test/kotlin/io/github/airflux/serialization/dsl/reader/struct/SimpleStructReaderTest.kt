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
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.reader.struct.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec

internal class SimpleStructReaderTest : FreeSpec() {

    companion object {

        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"

        private val ENV_WITH_FAIL_FAST_IS_TRUE = JsReaderEnv(EB(), OPTS(failFast = true))
        private val ENV_WITH_FAIL_FAST_IS_FALSE = JsReaderEnv(EB(), OPTS(failFast = false))

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
        private val IntReader: JsReader<EB, OPTS, Int> = DummyReader.int()
    }

    init {

        "The SimpleStructReader type" - {

            "when was created reader" - {
                val reader: JsStructReader<EB, OPTS, DTO> = structReader {
                    val id = property(required(name = ID_PROPERTY_NAME, reader = IntReader))
                    val name = property(optional(name = NAME_PROPERTY_NAME, reader = StringReader))
                    returns { _, location ->
                        DTO(id = +id, name = +name).toSuccess(location)
                    }
                }

                "when the source is not the struct type" - {
                    val source = JsString("")

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsStruct.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsStruct.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }
                }

                "when the source contains all properties" - {
                    val source = JsStruct(
                        ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE),
                        NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                    )

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                            )
                        }
                    }
                }

                "when the source does not contain required properties" - {
                    val source = JsStruct(
                        NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                    )

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }
                }

                "when the source does not contain optional properties" - {
                    val source = JsStruct(
                        ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE)
                    )

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = null)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = null)
                            )
                        }
                    }
                }

                "when the source contains the required property of an invalid type" - {
                    val source = JsStruct(
                        ID_PROPERTY_NAME to JsString(""),
                        NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                    )

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then the reader should return an error" {
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

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
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

                "when the source contains the optional property of an invalid type" - {
                    val source = JsStruct(
                        ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE),
                        NAME_PROPERTY_NAME to JsBoolean.valueOf(true),
                    )

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(NAME_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsString.nameOfType),
                                    actual = JsBoolean.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(NAME_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsString.nameOfType),
                                    actual = JsBoolean.nameOfType
                                )
                            )
                        }
                    }
                }

                "when the source contains all properties of an invalid type" - {
                    val source = JsStruct(
                        ID_PROPERTY_NAME to JsString(""),
                        NAME_PROPERTY_NAME to JsBoolean.valueOf(true)
                    )

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                        "then the reader should return first error" {
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

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                        "then the reader should return all errors" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsNumeric.Integer.nameOfType),
                                        actual = JsString.nameOfType
                                    )
                                ),
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsString.nameOfType),
                                        actual = JsBoolean.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }

            "when the result builder throw some exception" - {
                val reader: JsReader<EB, OPTS, DTO> = structReader {
                    returns { _, _ ->
                        throw InternalException()
                    }
                }

                val source = JsStruct(
                    ID_PROPERTY_NAME to JsNumeric.valueOf(ID_PROPERTY_VALUE),
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

    internal class InternalException : RuntimeException()

    internal data class DTO(val id: Int, val name: String?)

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
