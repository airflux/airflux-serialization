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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ReaderTest : FreeSpec() {

    companion object {
        private const val VALUE = "42"
        private const val LEFT_VALUE = "true"
        private const val RIGHT_VALUE = "false"
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The Reader type" - {

            "Reader#map" - {
                val reader: Reader<EB, Unit, String> = dummyStringReader()

                "should return new reader" {
                    val source = StringNode(VALUE)
                    val transformedReader = reader.map { value -> value.toInt() }
                    val result = transformedReader.read(ENV, LOCATION, source)

                    result shouldBe ReaderResult.Success(location = LOCATION, value = VALUE.toInt())
                }
            }

            "Reader#flatMapResult" - {
                val reader: Reader<EB, Unit, String> = dummyStringReader()

                "should return new reader" {
                    val source = StringNode(VALUE)
                    val transformedReader =
                        reader.flatMapResult { _, location, value ->
                            value.toInt().success(location)
                        }
                    val result = transformedReader.read(ENV, LOCATION, source)

                    result shouldBe ReaderResult.Success(location = LOCATION, value = VALUE.toInt())
                }
            }

            "Reader#or" - {

                "when left reader returns an value" - {
                    val leftReader: Reader<EB, Unit, String> =
                        DummyReader(ReaderResult.Success(location = LOCATION, value = LEFT_VALUE))
                    val rightReader: Reader<EB, Unit, String> =
                        DummyReader(ReaderResult.Success(location = LOCATION, value = RIGHT_VALUE))

                    val reader = leftReader or rightReader

                    "then the right reader doesn't execute" {
                        val result = reader.read(ENV, LOCATION, NullNode)
                        result shouldBe ReaderResult.Success(location = LOCATION, value = LEFT_VALUE)
                    }
                }

                "when left reader returns an error" - {
                    val leftReader: Reader<EB, Unit, String> = DummyReader { _, _, _ ->
                        ReaderResult.Failure(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                    }

                    "when the right reader returns an value" - {
                        val rightReader: Reader<EB, Unit, String> =
                            DummyReader(ReaderResult.Success(location = LOCATION, value = RIGHT_VALUE))

                        val reader = leftReader or rightReader

                        "then the result of the right reader should be returned" {
                            val result = reader.read(ENV, LOCATION, NullNode)
                            result shouldBe ReaderResult.Success(location = LOCATION, value = RIGHT_VALUE)
                        }
                    }

                    "when the right reader returns an error" - {
                        val rightReader: Reader<EB, Unit, String> = DummyReader { _, _, _ ->
                            ReaderResult.Failure(
                                location = LOCATION.append("identifier"),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = NumericNode.Integer.nameOfType
                                )
                            )
                        }
                        val reader = leftReader or rightReader

                        "then both errors should be returned" {
                            val result = reader.read(ENV, LOCATION, NullNode)

                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(

                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append("id"),
                                    error = JsonErrors.PathMissing
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append("identifier"),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = NumericNode.Integer.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
