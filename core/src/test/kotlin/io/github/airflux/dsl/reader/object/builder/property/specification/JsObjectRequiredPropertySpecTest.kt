/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.dsl.reader.`object`.builder.property.specification

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.JsPaths
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.github.airflux.std.reader.IntReader
import io.github.airflux.std.reader.StringReader
import io.github.airflux.std.validator.condition.applyIfNotNull
import io.github.airflux.std.validator.string.IsNotEmptyStringValidator
import io.github.airflux.std.validator.string.StringValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsObjectRequiredPropertySpecTest : FreeSpec() {

    companion object {
        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"

        private val CONTEXT =
            JsReaderContext(
                listOf(
                    IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty },
                    PathMissingErrorBuilder { JsonErrors.PathMissing },
                    InvalidTypeErrorBuilder(JsonErrors::InvalidType)
                )
            )
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsObjectPropertySpec#Defaultable" - {

            "when creating the instance by a attribute name" - {
                val spec = required(name = "id", reader = StringReader)

                "then the paths parameter must contain only the passed path" {
                    spec.path.items shouldContainExactly listOf(JsPath("id"))
                }

                "when the reader has read an attribute named id" - {

                    "if the attribute value is not the null type" - {
                        val input = JsObject("id" to JsString(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then the not-null value should be returned" {
                            result as JsResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }
                }

                "when the attribute does not founded" - {
                    val input = JsObject("code" to JsString(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(CONTEXT, LOCATION, input)

                    "then an error should be returned" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                        )
                    }
                }

                "when a read error occurred" - {
                    val input = JsObject("id" to JsNumber.valueOf(10))
                    val result = spec.reader.read(CONTEXT, LOCATION, input)

                    "then should be returned a read error" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when creating the instance by a single-path" - {
                val path = JsPath("id")
                val spec = required(path = path, reader = StringReader)

                "then the paths parameter must contain only the passed path" {
                    spec.path.items shouldContainExactly listOf(path)
                }

                "when the reader has read an attribute named id" - {

                    "if the attribute value is not the null type" - {
                        val input = JsObject("id" to JsString(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then the not-null value should be returned" {
                            result as JsResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }
                }

                "when the attribute does not founded" - {
                    val input = JsObject("code" to JsString(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(CONTEXT, LOCATION, input)

                    "then an error should be returned" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                        )
                    }
                }

                "when an error occurs while reading" - {
                    val input = JsObject("id" to JsNumber.valueOf(10))
                    val result = spec.reader.read(CONTEXT, LOCATION, input)

                    "then should be returned a read error" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when creating the instance by a multi-path" - {
                val idPath = JsPath("id")
                val identifierPath = JsPath("identifier")
                val spec = required(paths = JsPaths(idPath, identifierPath), reader = StringReader)

                "then the paths parameter must contain only the passed paths" {
                    spec.path.items shouldContainExactly listOf(idPath, identifierPath)
                }

                "when the reader has read an attribute named id" - {

                    "if the attribute value is not the null type" - {
                        val input = JsObject("id" to JsString(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then the not-null value should be returned" {
                            result as JsResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }
                }

                "when the reader has read an attribute named identifier" - {

                    "if the attribute value is not the null type" - {
                        val input = JsObject("identifier" to JsString(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then the not-null value should be returned" {
                            result as JsResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }
                }

                "when the attribute does not founded" - {
                    val input = JsObject("code" to JsString(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(CONTEXT, LOCATION, input)

                    "then all errors should be returned" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.PathMissing
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION.append("identifier"),
                                error = JsonErrors.PathMissing
                            )
                        )
                    }
                }

                "when an error occurs while reading" - {
                    val input = JsObject("id" to JsNumber.valueOf(10))
                    val result = spec.reader.read(CONTEXT, LOCATION, input)

                    "then should be returned a read error" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = JsObjectPropertySpec.Required(path = JsPaths(JsPath("id")), reader = StringReader)
                val specWithValidator = spec.validation(StringValidator.isNotEmpty.applyIfNotNull())

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val input = JsString(ID_VALUE_AS_UUID)

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val input = JsString("")

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(location = LOCATION, error = JsonErrors.Validation.Strings.IsEmpty)
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val input = JsNumber.valueOf(10)

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = required(name = "id", reader = StringReader)
                val alt = required(name = "id", reader = IntReader.map { it.toString() })
                val specWithAlternative = spec or alt

                "then the paths parameter must contain all elements from both spec" {
                    specWithAlternative.path.items shouldContainExactly listOf(JsPath("id"))
                }

                "when the main reader has successfully read" - {
                    val input = JsObject("id" to JsString(ID_VALUE_AS_UUID))
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then a value should be returned" {
                        println(result)
                        result as JsResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }
                }

                "when the main reader has failure read" - {
                    val input = JsObject("id" to JsNumber.valueOf(ID_VALUE_AS_INT)!!)
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then a value should be returned from the alternative reader" {
                        result as JsResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_INT
                    }
                }

                "when the alternative reader has failure read" - {
                    val input = JsObject("id" to JsBoolean.True)
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then should be returned all read errors" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append("id"),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.BOOLEAN
                                )
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION.append("id"),
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
}
