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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec

internal class JsPathReaderBuildersTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_PROPERTY_VALUE_AS_INT = "10"
        private const val CODE_PROPERTY_NAME = "code"
        private const val DEFAULT_VALUE = "2a100f64-0cef-4cca-90c0-5148f71c5fca"

        private val ENV = JsReaderEnv(config = JsReaderEnv.Config(EB(), Unit))
        private val LOCATION: JsLocation = JsLocation

        private val StringReader: JsReader<EB, Unit, String> = DummyReader.string()
    }

    init {

        "The builders of JsPathReader type" - {

            "the `readRequired` function" - {
                val reader = JsPath(ID_PROPERTY_NAME).readRequired(reader = StringReader)

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result.shouldBeSuccess(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = reader.read(ENV, LOCATION, source)

                        "then should be returned a read error" {
                            result.shouldBeFailure(
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
                    val result = reader.read(ENV, LOCATION, source)

                    "then an error should be returned" {
                        result.shouldBeFailure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }
            }

            "the `readRequired` function with predicate" - {

                "when the predicate returns the true value" - {
                    val reader =
                        JsPath(ID_PROPERTY_NAME).readRequired(reader = StringReader, predicate = { _, _ -> true })

                    "when the property is present" - {

                        "when a read is success" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                            val result = reader.read(ENV, LOCATION, source)

                            "then a value should be returned" {
                                result.shouldBeSuccess(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    value = ID_PROPERTY_VALUE_AS_UUID
                                )
                            }
                        }

                        "when a read error occurred" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                            val result = reader.read(ENV, LOCATION, source)

                            "then should be returned a read error" {
                                result.shouldBeFailure(
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
                        val result = reader.read(ENV, LOCATION, source)

                        "then an error should be returned" {
                            result.shouldBeFailure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }
                }

                "when the predicate returns the false value" - {
                    val reader =
                        JsPath(ID_PROPERTY_NAME).readRequired(reader = StringReader, predicate = { _, _ -> false })

                    "when the property is present" - {

                        "when a read is success" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                            val result = reader.read(ENV, LOCATION, source)

                            "then a value should be returned" {
                                result.shouldBeSuccess(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    value = ID_PROPERTY_VALUE_AS_UUID
                                )
                            }
                        }

                        "when a read error occurred" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                            val result = reader.read(ENV, LOCATION, source)

                            "then should be returned a read error" {
                                result.shouldBeFailure(
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
                        val result = reader.read(ENV, LOCATION, source)

                        "then the null value should be returned" {
                            result.shouldBeSuccess(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = null
                            )
                        }
                    }
                }
            }

            "the `readOptional` function" - {
                val reader = JsPath(ID_PROPERTY_NAME).readOptional(reader = StringReader)

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result.shouldBeSuccess(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = reader.read(ENV, LOCATION, source)

                        "then should be returned a read error" {
                            result.shouldBeFailure(
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
                    val result = reader.read(ENV, LOCATION, source)

                    "then the null value should be returned" {
                        result.shouldBeSuccess(location = LOCATION.append(ID_PROPERTY_NAME), value = null)
                    }
                }
            }

            "the `readOptional` function with the generator of default value" - {
                val reader =
                    JsPath(ID_PROPERTY_NAME).readOptional(reader = StringReader, default = { _, _ -> DEFAULT_VALUE })

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result.shouldBeSuccess(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = reader.read(ENV, LOCATION, source)

                        "then should be returned a read error" {
                            result.shouldBeFailure(
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
                    val result = reader.read(ENV, LOCATION, source)

                    "then a default value should be returned" {
                        result.shouldBeSuccess(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = DEFAULT_VALUE
                        )
                    }
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
