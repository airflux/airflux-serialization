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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.map
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.dummy.DummyValidator
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class RequiredPropertySpecTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val CODE_PROPERTY_NAME = "code"

        private const val ID_PROPERTY_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_PROPERTY_VALUE_AS_INT = "10"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation

        private val StringReader: JsReader<EB, Unit, String> = DummyReader.string()
        private val IntReader: JsReader<EB, Unit, Int> = DummyReader.int()

        private val IsNotEmptyStringValidator: JsValidator<EB, Unit, String?> =
            DummyValidator.isNotEmptyString { JsonErrors.Validation.Strings.IsEmpty }
    }

    init {

        "The RequiredPropertySpec type" - {

            "when creating the instance by a property name" - {
                val spec = required(name = ID_PROPERTY_NAME, reader = StringReader)

                "then the paths parameter must contain only the passed name" {
                    spec.paths shouldBe JsPaths(JsPath(ID_PROPERTY_NAME))
                }

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then should be returned a read error" {
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        }
                    }
                }

                "when the property is missing" - {
                    val source = JsStruct(CODE_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then an error should be returned" {
                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }
            }

            "when creating the instance by a property path" - {
                val path = JsPath(ID_PROPERTY_NAME)
                val spec = required(path = path, reader = StringReader)

                "then the paths parameter must contain only the passed path" {
                    spec.paths shouldBe JsPaths(path)
                }

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then should be returned a read error" {
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        }
                    }
                }

                "when the property is missing" - {
                    val source = JsStruct(CODE_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then an error should be returned" {
                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = required(name = ID_PROPERTY_NAME, reader = StringReader)
                val specWithValidator = spec.validation(IsNotEmptyStringValidator)

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE_AS_UUID
                        )
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(""))

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when a read error occurred" - {

                    "then should be returned a read error" {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.STRING,
                                actual = JsValue.Type.NUMBER
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = required(name = ID_PROPERTY_NAME, reader = StringReader)
                val alt = required(name = ID_PROPERTY_NAME, reader = IntReader.map { it.toString() })
                val specWithAlternative = spec or alt

                "then the paths parameter must contain all elements from both spec" {
                    specWithAlternative.paths shouldBe JsPaths(JsPath(ID_PROPERTY_NAME))
                }

                "when the main reader has successfully read" - {
                    val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE_AS_UUID
                        )
                    }
                }

                "when the main reader has failure read" - {
                    val source =
                        JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned from the alternative reader" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE_AS_INT
                        )
                    }
                }

                "when the alternative reader has failure read" - {
                    val source = JsStruct(ID_PROPERTY_NAME to JsBoolean.True)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then should be returned all read errors" {
                        result.shouldBeFailure(
                            JsReaderResult.Failure.Cause(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.BOOLEAN
                                )
                            ),
                            JsReaderResult.Failure.Cause(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.BOOLEAN
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

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing

        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
