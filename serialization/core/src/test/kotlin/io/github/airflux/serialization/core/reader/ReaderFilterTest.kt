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
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.dummy.DummyReaderPredicate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReaderFilterTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val CODE_PROPERTY_NAME = "code"
        private const val ID_PROPERTY_VALUE = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val CODE_PROPERTY_VALUE = "code"

        private val ENV: JsReaderEnv<EB, Unit> = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation

        private val StringReader: JsReader<EB, Unit, String> = DummyReader.string()
    }

    init {
        "The extension-function JsReader#filter" - {

            "when an original reader returns a result as a success" - {

                "when the value in the result is not null" - {
                    val requiredReader: JsReader<EB, Unit, String> = DummyReader { env, location, source ->
                        val lookup = source.lookup(location, JsPath(ID_PROPERTY_NAME))
                        readRequired(env, lookup, StringReader)
                    }

                    val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                    "when the value satisfies the predicate" - {
                        val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = true)

                        "then filter should return the original value" {
                            val filtered = requiredReader.filter(predicate)
                                .read(ENV, LOCATION, source)

                            filtered shouldBe success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_PROPERTY_VALUE
                            )
                        }
                    }

                    "when the value does not satisfy the predicate" - {
                        val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = false)

                        "then filter should return the null value" {
                            val filtered = requiredReader.filter(predicate)
                                .read(ENV, LOCATION, source)

                            filtered shouldBe success(location = LOCATION.append(ID_PROPERTY_NAME), value = null)
                        }
                    }
                }

                "when the value in the result is null" - {
                    val optionalReader: JsReader<EB, Unit, String?> = DummyReader { env, location, source ->
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

    internal class EB : PathMissingErrorBuilder,
                        InvalidTypeErrorBuilder {

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing

        override fun invalidTypeError(expected: Iterable<JsValue.Type>, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
