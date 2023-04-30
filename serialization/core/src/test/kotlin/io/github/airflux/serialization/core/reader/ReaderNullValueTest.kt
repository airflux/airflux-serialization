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
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.struct.readOptional
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReaderNullValueTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val CODE_PROPERTY_NAME = "code"
        private const val ID_PROPERTY_VALUE = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val CODE_PROPERTY_VALUE = "code"
        private const val ALTERNATIVE_VALUE = "42"

        private val ENV: ReaderEnv<EB, Unit> = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location

        private val stringReader = DummyReader.string<EB, Unit, Unit>()
    }

    init {
        "The extension-function Reader#ifNullValue" - {
            val reader = DummyReader<EB, Unit, Unit, String?> { env, context, location, source ->
                val lookup = source.lookup(location, JsPath(ID_PROPERTY_NAME))
                readOptional(env, context, lookup, stringReader)
            }.ifNullValue { _, _, _ -> ALTERNATIVE_VALUE }

            "when an original reader returns a result as a success" - {

                "when the value is not null" - {
                    val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                    "then should return the original value" {
                        val result: ReadingResult<String?> = reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_PROPERTY_VALUE
                        )
                    }
                }

                "when the value is null" - {
                    val source = StructNode(CODE_PROPERTY_NAME to StringNode(CODE_PROPERTY_VALUE))

                    "then should return the default value" {
                        val result: ReadingResult<String?> = reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeSuccess success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ALTERNATIVE_VALUE
                        )
                    }
                }
            }

            "when an original reader returns a result as a failure" - {
                val source = StructNode(ID_PROPERTY_NAME to BooleanNode.True)

                "then should return the original value" {
                    val result: ReadingResult<String?> = reader.read(ENV, CONTEXT, LOCATION, source)

                    result shouldBe failure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.InvalidType(
                            expected = listOf(StringNode.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
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
