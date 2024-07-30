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
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.test.dummy.DummyPathReader
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.dummy.DummyReaderPredicate
import io.github.airflux.serialization.test.dummy.DummyValidator
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsPathReaderTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_PROPERTY_VALUE_AS_INT = "10"
        private const val CODE_PROPERTY_NAME = "code"
        private const val CODE_PROPERTY_VALUE = "code"
        private const val LEFT_VALUE = "true"
        private const val RIGHT_VALUE = "false"
        private const val DEFAULT_VALUE = "2a100f64-0cef-4cca-90c0-5148f71c5fca"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation

        private val StringReader: JsReader<EB, Unit, String> = DummyReader.string()
        private val READER: JsPathReader<EB, Unit, String> = DummyPathReader { env, location, source ->
            val lookup = source.lookup(location, JsPath(ID_PROPERTY_NAME))
            readRequired(env, lookup, StringReader)
        }
    }

    init {

        "The extension functions of the JsPathReader type" - {

            "the `or` function" - {

                "when left reader returns an value" - {
                    val leftReader: JsPathReader<EB, Unit, String> =
                        DummyPathReader(success(location = LOCATION, value = LEFT_VALUE))
                    val rightReader: JsPathReader<EB, Unit, String> =
                        DummyPathReader(success(location = LOCATION, value = RIGHT_VALUE))

                    val reader = leftReader or rightReader

                    "then the right reader doesn't execute" {
                        val result = reader.read(ENV, LOCATION, JsNull)
                        result shouldBeSuccess success(location = LOCATION, value = LEFT_VALUE)
                    }
                }

                "when left reader returns an error" - {
                    val leftReader: JsPathReader<EB, Unit, String> = DummyPathReader { _, _, _ ->
                        failure(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                    }

                    "when the right reader returns an value" - {
                        val rightReader: JsPathReader<EB, Unit, String> =
                            DummyPathReader(success(location = LOCATION, value = RIGHT_VALUE))

                        val reader = leftReader or rightReader

                        "then the result of the right reader should be returned" {
                            val result = reader.read(ENV, LOCATION, JsNull)
                            result shouldBeSuccess success(location = LOCATION, value = RIGHT_VALUE)
                        }
                    }

                    "when the right reader returns an error" - {
                        val rightReader: JsPathReader<EB, Unit, String> = DummyPathReader { _, _, _ ->
                            failure(
                                location = LOCATION.append("identifier"),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        }
                        val reader = leftReader or rightReader

                        "then both errors should be returned" {
                            val result = reader.read(ENV, LOCATION, JsNull)

                            result.shouldBeFailure(
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append("id"),
                                    error = JsonErrors.PathMissing
                                ),
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append("identifier"),
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.NUMBER
                                    )
                                )
                            )
                        }
                    }
                }
            }

            "the `validation` function" - {

                "when an original reader returns a result as a success" - {
                    val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))

                    "when validation is a success" - {
                        val validator: JsValidator<EB, Unit, String> = DummyValidator(result = valid())

                        "then should return the original result" {
                            val result = READER.validation(validator)
                                .read(ENV, LOCATION, source)
                            result shouldBe success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when validation is a failure" - {
                        val validator: JsValidator<EB, Unit, String> = DummyValidator(
                            result = invalid(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        )

                        "then should return the result of a validation" {
                            val result = READER.validation(validator)
                                .read(ENV, LOCATION, source)

                            result shouldBe failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        }
                    }
                }

                "when an original reader returns a result as a failure" - {
                    val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))

                    "then validation does not execute and the original result should be returned" {
                        val validator: JsValidator<EB, Unit, String> = DummyValidator { _, location, _ ->
                            invalid(location = location, error = JsonErrors.Validation.Strings.IsEmpty)
                        }

                        val result = READER.validation(validator)
                            .read(ENV, LOCATION, source)
                        result shouldBe failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }
            }

            "the `filter` function" - {

                "when an original reader returns a result as a success" - {

                    "when the value in the result is not null" - {

                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))

                        "when the value satisfies the predicate" - {
                            val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = true)

                            "then filter should return the original value" {
                                val filtered = READER.filter(predicate)
                                    .read(ENV, LOCATION, source)

                                filtered shouldBe success(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    value = ID_PROPERTY_VALUE_AS_UUID
                                )
                            }
                        }

                        "when the value does not satisfy the predicate" - {
                            val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = false)

                            "then filter should return the null value" {
                                val filtered = READER.filter(predicate)
                                    .read(ENV, LOCATION, source)

                                filtered shouldBe success(location = LOCATION.append(ID_PROPERTY_NAME), value = null)
                            }
                        }
                    }

                    "when the value in the result is null" - {
                        val optionalReader: JsReader<EB, Unit, String?> =
                            DummyReader { env, location, source ->
                                val lookup = source.lookup(location, JsPath(ID_PROPERTY_NAME))
                                readOptional(env, lookup, StringReader)
                            }
                        val source = JsStruct(CODE_PROPERTY_NAME to JsString(CODE_PROPERTY_VALUE))
                        val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate { _, _, _ ->
                            throw io.kotest.assertions.failure("Predicate not called.")
                        }

                        "then the filter should not be applying" {
                            val filtered = optionalReader.filter(predicate)
                                .read(ENV, LOCATION, source)
                            filtered shouldBe success(location = LOCATION.append(ID_PROPERTY_NAME), value = null)
                        }
                    }
                }

                "when an original reader returns a result as a failure" - {
                    val requiredReader: JsReader<EB, Unit, String> = DummyReader { env, location, source ->
                        val lookup = source.lookup(location, JsPath(ID_PROPERTY_NAME))
                        readRequired(env, lookup, StringReader)
                    }
                    val source = JsStruct(CODE_PROPERTY_NAME to JsString(CODE_PROPERTY_VALUE))
                    val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate { _, _, _ ->
                        throw io.kotest.assertions.failure("Predicate not called.")
                    }

                    "then the filter should not be applying" {
                        val filtered = requiredReader.filter(predicate)
                            .read(ENV, LOCATION, source)
                        filtered shouldBe failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }
            }
        }

        "The static functions of JsPathReader type" - {

            "the `optional` function" - {
                val reader = JsPathReader.optional(path = JsPath(ID_PROPERTY_NAME), reader = StringReader)

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = reader.read(ENV, LOCATION, source)

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
                    val result = reader.read(ENV, LOCATION, source)

                    "then the null value should be returned" {
                        result shouldBeSuccess success(location = LOCATION.append(ID_PROPERTY_NAME), value = null)
                    }
                }
            }

            "the `optional` function with the generator of default value" - {
                val reader = JsPathReader.optional(
                    path = JsPath(ID_PROPERTY_NAME),
                    reader = StringReader,
                    default = { _, _ -> DEFAULT_VALUE })

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = reader.read(ENV, LOCATION, source)

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
                    val result = reader.read(ENV, LOCATION, source)

                    "then a default value should be returned" {
                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = DEFAULT_VALUE
                        )
                    }
                }
            }

            "the `required` function" - {
                val reader = JsPathReader.required(path = JsPath(ID_PROPERTY_NAME), reader = StringReader)

                "when the property is present" - {

                    "when a read is success" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                        val result = reader.read(ENV, LOCATION, source)

                        "then a value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE_AS_UUID
                            )
                        }
                    }

                    "when a read error occurred" - {
                        val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                        val result = reader.read(ENV, LOCATION, source)

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
                    val result = reader.read(ENV, LOCATION, source)

                    "then an error should be returned" {
                        result shouldBeFailure failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }
            }

            "the `required` function with predicate" - {

                "when the predicate returns the true value" - {
                    val reader = JsPathReader.required(
                        path = JsPath(ID_PROPERTY_NAME),
                        reader = StringReader,
                        predicate = { _, _ -> true }
                    )

                    "when the property is present" - {

                        "when a read is success" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                            val result = reader.read(ENV, LOCATION, source)

                            "then a value should be returned" {
                                result shouldBeSuccess success(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    value = ID_PROPERTY_VALUE_AS_UUID
                                )
                            }
                        }

                        "when a read error occurred" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                            val result = reader.read(ENV, LOCATION, source)

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
                        val result = reader.read(ENV, LOCATION, source)

                        "then an error should be returned" {
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }
                }

                "when the predicate returns the false value" - {
                    val reader = JsPathReader.required(
                        path = JsPath(ID_PROPERTY_NAME),
                        reader = StringReader,
                        predicate = { _, _ -> false }
                    )

                    "when the property is present" - {

                        "when a read is success" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE_AS_UUID))
                            val result = reader.read(ENV, LOCATION, source)

                            "then a value should be returned" {
                                result shouldBeSuccess success(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    value = ID_PROPERTY_VALUE_AS_UUID
                                )
                            }
                        }

                        "when a read error occurred" - {
                            val source = JsStruct(ID_PROPERTY_NAME to JsNumber.valueOf(ID_PROPERTY_VALUE_AS_INT)!!)
                            val result = reader.read(ENV, LOCATION, source)

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
                        val result = reader.read(ENV, LOCATION, source)

                        "then the null value should be returned" {
                            result shouldBeSuccess success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = null
                            )
                        }
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
