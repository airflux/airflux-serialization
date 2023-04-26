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
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec

internal class ReaderFlatMapTest : FreeSpec() {

    companion object {
        private const val VALUE = "42"
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
    }

    init {
        "The extension-function Reader#flatMapResult" - {

            "when the original reader returns a successful result" - {
                val reader: Reader<EB, Unit, Unit, String> = DummyReader.string()

                "then the new reader should return the transformed value" {
                    val source = StringNode(VALUE)
                    val transformedReader =
                        reader.flatMapResult { _, _, location, value ->
                            value.toInt().success(location)
                        }
                    val result = transformedReader.read(ENV, CONTEXT, LOCATION, source)

                    result shouldBeSuccess ReadingResult.Success(location = LOCATION, value = VALUE.toInt())
                }
            }

            "when the original reader returns an error" - {
                val failure = ReadingResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                val reader: Reader<EB, Unit, Unit, String> = DummyReader(result = failure)

                "then the new reader should return it error" {
                    val source = StringNode(VALUE)
                    val transformedReader =
                        reader.flatMapResult { _, _, location, value ->
                            value.toInt().success(location)
                        }
                    val result = transformedReader.read(ENV, CONTEXT, LOCATION, source)

                    result shouldBeFailure failure
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
