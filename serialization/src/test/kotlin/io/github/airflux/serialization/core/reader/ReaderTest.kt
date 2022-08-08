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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ReaderTest : FreeSpec() {

    companion object {
        private const val VALUE = "42"
        private const val LEFT_VALUE = "2b26f8fa-dfdf-40bd-82ba-6e7cde08036d"
        private const val RIGHT_VALUE = "bbbbb1f0-606e-4bd3-9ccb-41b52e6287f2"
        private val CONTEXT = ReaderContext()
        private val LOCATION = Location.empty
    }

    init {

        "The Reader type" - {

            "Reader#map" - {
                val reader =
                    DummyReader { _, location -> ReaderResult.Success(location = location.append("id"), value = VALUE) }

                "should return new reader" {
                    val transformedReader = reader.map { value -> value.toInt() }
                    val result = transformedReader.read(CONTEXT, LOCATION, NullNode)

                    result shouldBe ReaderResult.Success(location = LOCATION.append("id"), value = VALUE.toInt())
                }
            }

            "Reader#flatMap" - {
                val reader =
                    DummyReader { _, location -> ReaderResult.Success(location = location.append("id"), value = VALUE) }

                "should return new reader" {
                    val transformedReader = reader.flatMap { _, location, value -> value.toInt().success(location) }
                    val result = transformedReader.read(CONTEXT, LOCATION, NullNode)

                    result shouldBe ReaderResult.Success(location = LOCATION.append("id"), value = VALUE.toInt())
                }
            }

            "Reader#or" - {

                "when left reader returns an value" - {
                    val leftReader = DummyReader { _, location ->
                        ReaderResult.Success(location = location.append("id"), value = LEFT_VALUE)
                    }

                    val rightReader = DummyReader { _, location ->
                        ReaderResult.Success(location = location.append("identifier"), value = RIGHT_VALUE)
                    }

                    val reader = leftReader or rightReader

                    "then the right reader doesn't execute" {
                        val result = reader.read(CONTEXT, LOCATION, NullNode)
                        result shouldBe ReaderResult.Success(location = LOCATION.append("id"), value = LEFT_VALUE)
                    }
                }

                "when left reader returns an error" - {
                    val leftReader = DummyReader { _, location ->
                        ReaderResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
                    }

                    "when the right reader returns an value" - {
                        val rightReader = DummyReader { _, location ->
                            ReaderResult.Success(location = location.append("identifier"), value = RIGHT_VALUE)
                        }
                        val reader = leftReader or rightReader

                        "then the result of the right reader should be returned" {
                            val result = reader.read(CONTEXT, LOCATION, NullNode)
                            result shouldBe ReaderResult.Success(
                                location = LOCATION.append("identifier"),
                                value = RIGHT_VALUE
                            )
                        }
                    }

                    "when the right reader returns an error" - {
                        val rightReader = DummyReader { _, location ->
                            ReaderResult.Failure(
                                location = location.append("identifier"),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        }
                        val reader = leftReader or rightReader

                        "then both errors should be returned" {
                            val result = reader.read(CONTEXT, LOCATION, NullNode)

                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(

                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append("id"),
                                    error = JsonErrors.PathMissing
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append("identifier"),
                                    error = JsonErrors.InvalidType(
                                        expected = ValueNode.Type.STRING,
                                        actual = ValueNode.Type.NUMBER
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
