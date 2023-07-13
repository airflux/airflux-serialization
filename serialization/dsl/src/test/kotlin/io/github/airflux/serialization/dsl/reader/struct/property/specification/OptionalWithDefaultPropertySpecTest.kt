/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.map
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.dummy.DummyValidator
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class OptionalWithDefaultPropertySpecTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val CODE_PROPERTY_NAME = "code"

        private const val ID_PROPERTY_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_PROPERTY_VALUE_AS_INT = "10"
        private const val DEFAULT_VALUE = "none"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private val DEFAULT = { _: JsReaderEnv<EB, Unit>, _: Unit -> DEFAULT_VALUE }

        private val StringReader: JsReader<EB, Unit, Unit, String> = DummyReader.string()
        private val IntReader: JsReader<EB, Unit, Unit, Int> = DummyReader.int()

        private val IsNotEmptyStringValidator: JsValidator<EB, Unit, Unit, String?> =
            DummyValidator.isNotEmptyString { JsonErrors.Validation.Strings.IsEmpty }
    }

    init {

        "The OptionalWithDefaultPropertySpec type" - {

            "when creating the instance by a property name" - {
                val spec = optional(name = ID_PROPERTY_NAME, reader = StringReader, default = DEFAULT)

                "then the paths parameter must contain only the passed name" {
                    spec.paths shouldBe JsPaths(JsPath(ID_PROPERTY_NAME))
                }

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumeric.valueOf(10))
                        val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                        "then should be returned a read error" {
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsString.nameOfType),
                                    actual = JsNumeric.Integer.nameOfType
                                )
                            )
                        }
                    }
                }

                "when the property is missing" - {
                    val source = JsStruct(CODE_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then a default value should be returned" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = DEFAULT_VALUE
                        )
                    }
                }
            }

            "when creating the instance by a property path" - {
                val path = JsPath(ID_PROPERTY_NAME)
                val spec = optional(path = path, reader = StringReader, default = DEFAULT)

                "then the paths parameter must contain only the passed path" {
                    spec.paths shouldBe JsPaths(path)
                }

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumeric.valueOf(10))
                        val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                        "then should be returned a read error" {
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsString.nameOfType),
                                    actual = JsNumeric.Integer.nameOfType
                                )
                            )
                        }
                    }
                }

                "when the property is missing" - {
                    val source = JsStruct(CODE_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then a default value should be returned" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = DEFAULT_VALUE
                        )
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = optional(name = ID_PROPERTY_NAME, reader = StringReader, default = DEFAULT)
                val specWithValidator = spec.validation(IsNotEmptyStringValidator)

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))

                        val result = specWithValidator.reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE_AS_UUID
                        )
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(""))

                        val result = specWithValidator.reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when a read error occurred" - {

                    "then should be returned a read error" {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumeric.valueOf(10))

                        val result = specWithValidator.reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.InvalidType(
                                expected = listOf(JsString.nameOfType),
                                actual = JsNumeric.Integer.nameOfType
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = optional(name = ID_PROPERTY_NAME, reader = StringReader, default = DEFAULT)
                val alt = optional(
                    name = ID_PROPERTY_NAME,
                    reader = IntReader.map { it.toString() },
                    default = DEFAULT
                )
                val specWithAlternative = spec or alt

                "then the paths parameter must contain all elements from both spec" {
                    specWithAlternative.paths shouldBe JsPaths(JsPath(ID_PROPERTY_NAME))
                }

                "when the main reader has successfully read" - {
                    val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                    val result = specWithAlternative.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then a value should be returned" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE_AS_UUID
                        )
                    }
                }

                "when the main reader has failure read" - {
                    val source =
                        JsStruct(ID_PROPERTY_NAME to JsNumeric.Integer.valueOrNullOf(ID_PROPERTY_VALUE_AS_INT)!!)
                    val result = specWithAlternative.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then a value should be returned from the alternative reader" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE_AS_INT
                        )
                    }
                }

                "when the alternative reader has failure read" - {
                    val source = JsStruct(ID_PROPERTY_NAME to JsBoolean.True)
                    val result = specWithAlternative.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then should be returned all read errors" {
                        result.shouldBeFailure(
                            ReadingResult.Failure.Cause(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsString.nameOfType),
                                    actual = JsBoolean.nameOfType
                                )
                            ),
                            ReadingResult.Failure.Cause(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsNumeric.Integer.nameOfType),
                                    actual = JsBoolean.nameOfType
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder,
                        InvalidTypeErrorBuilder {
        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
