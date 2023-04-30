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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.DummyValidator
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.struct.readRequired
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReaderValidationTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "91a10692-7430-4d58-a465-633d45ea2f4b"

        private val ENV: JsReaderEnv<EB, Unit> = JsReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation

        private val stringReader = DummyReader.string<EB, Unit, Unit>()
    }

    init {
        "The extension function JsReader#validation" - {
            val requiredReader = DummyReader<EB, Unit, Unit, String> { env, context, location, source ->
                val lookup = source.lookup(location, JsPath(ID_PROPERTY_NAME))
                readRequired(env, context, lookup, stringReader)
            }

            "when an original reader returns a result as a success" - {
                val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                "when validation is a success" - {
                    val validator: JsValidator<EB, Unit, Unit, String> = DummyValidator(result = valid())

                    "then should return the original result" {
                        val result = requiredReader.validation(validator).read(ENV, CONTEXT, LOCATION, source)
                        result shouldBe success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE
                        )
                    }
                }

                "when validation is a failure" - {
                    val validator: JsValidator<EB, Unit, Unit, String> = DummyValidator(
                        result = invalid(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    )

                    "then should return the result of a validation" {
                        val result = requiredReader.validation(validator).read(ENV, CONTEXT, LOCATION, source)

                        result shouldBe failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }
            }

            "when an original reader returns a result as a failure" - {
                val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                "then validation does not execute and the original result should be returned" {
                    val validator: JsValidator<EB, Unit, Unit, String> = DummyValidator { _, _, location, _ ->
                        invalid(location = location, error = JsonErrors.Validation.Strings.IsEmpty)
                    }

                    val result = requiredReader.validation(validator).read(ENV, CONTEXT, LOCATION, source)
                    result shouldBe failure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.Validation.Strings.IsEmpty
                    )
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
