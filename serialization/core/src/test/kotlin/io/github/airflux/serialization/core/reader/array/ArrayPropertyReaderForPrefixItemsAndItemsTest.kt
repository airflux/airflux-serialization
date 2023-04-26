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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import kotlin.reflect.KClass

internal class ArrayPropertyReaderForPrefixItemsAndItemsTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val THIRD_PHONE_VALUE = "789"

        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val IntReader: Reader<EB, OPTS, Unit, Int> = DummyReader.int()
        private val StringReader: Reader<EB, OPTS, Unit, String> = DummyReader.string()
    }

    init {

        "The readArray function for the prefix-items and items readers" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "then reader should return result" {
                        val result: ReadingResult<List<String>> = readArray(
                            env = envWithFailFastIsTrue,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItemReaders = listOf(StringReader),
                            itemsReader = StringReader
                        )

                        result shouldBeSuccess ReadingResult.Success(location = LOCATION, value = emptyList())
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "then reader should return result" {
                        val result: ReadingResult<List<String>> = readArray(
                            env = envWithFailFastIsFalse,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItemReaders = listOf(StringReader),
                            itemsReader = StringReader
                        )

                        result shouldBeSuccess ReadingResult.Success(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {
                val source = ArrayNode(
                    StringNode(FIRST_PHONE_VALUE),
                    StringNode(SECOND_PHONE_VALUE),
                    StringNode(THIRD_PHONE_VALUE),
                )

                "when read was no errors" - {

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return result" {
                            val result = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(StringReader),
                                itemsReader = StringReader
                            )

                            result shouldBeSuccess ReadingResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return result" {
                            val result = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(StringReader),
                                itemsReader = StringReader
                            )

                            result shouldBeSuccess ReadingResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return first error" {
                            val result: ReadingResult<List<Int>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader),
                                itemsReader = IntReader
                            )

                            result shouldBeFailure ReadingResult.Failure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return all errors" {
                            val result = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader, StringReader),
                                itemsReader = IntReader
                            )

                            result.shouldBeFailure(
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        ValueCastErrorBuilder {

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected, actual)

        override fun valueCastError(value: String, target: KClass<*>): ReadingResult.Error =
            JsonErrors.ValueCast(value, target)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
