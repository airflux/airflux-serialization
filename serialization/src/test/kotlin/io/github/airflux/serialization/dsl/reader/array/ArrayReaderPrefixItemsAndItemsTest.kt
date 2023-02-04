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

package io.github.airflux.serialization.dsl.reader.array

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyBooleanReader
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.common.kotest.shouldBeFailure
import io.github.airflux.serialization.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.dsl.reader.array.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.item.specification.prefixItems
import io.kotest.core.spec.style.FreeSpec

internal class ArrayReaderPrefixItemsAndItemsTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "first"
        private const val SECOND_ITEM = "second"
        private const val THIRD_ITEM = "third"

        private val CONTEXT = Unit
        private val LOCATION = Location.empty

        private val StringReader = dummyStringReader<EB, OPTS, Unit>()
        private val BooleanReader = dummyBooleanReader<EB, OPTS, Unit>()
    }

    init {

        "The ArrayReader type" - {

            "when a reader was created for prefixItems and items" - {
                val reader: Reader<EB, OPTS, Unit, List<Any>> = arrayReader {
                    returns(
                        prefixItems = prefixItems(
                            nonNullable(StringReader),
                            nonNullable(StringReader)
                        ),
                        items = nonNullable(BooleanReader)
                    )
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "when source is not the array type" - {
                        val source = StringNode("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(ArrayNode.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the number of items is less than the number of elements in prefixItems" - {
                        val source = ArrayNode(StringNode(FIRST_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeSuccess listOf(FIRST_ITEM)
                        }
                    }

                    "when the number of items is equal to the number of elements in prefixItems" - {
                        val source =
                            ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeSuccess listOf(FIRST_ITEM, SECOND_ITEM)
                        }
                    }

                    "when the number of items is more than the number of elements in prefixItems" - {

                        "when additional items are of valid type" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                BooleanNode.valueOf(true)
                            )

                            "then should return all items read" {
                                val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                                result shouldBeSuccess listOf(FIRST_ITEM, SECOND_ITEM, true)
                            }
                        }

                        "when additional items are of invalid type" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                StringNode(THIRD_ITEM),
                                NumericNode.Integer.valueOf(10)
                            )

                            "then should return an error" {
                                val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                                result shouldBeFailure ReaderResult.Failure(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(BooleanNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            }
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "when source is not the array type" - {
                        val source = StringNode("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(ArrayNode.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the number of items is less than the number of elements in prefixItems" - {
                        val source = ArrayNode(StringNode(FIRST_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeSuccess listOf(FIRST_ITEM)
                        }
                    }

                    "when the number of items is equal to the number of elements in prefixItems" - {
                        val source =
                            ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeSuccess listOf(FIRST_ITEM, SECOND_ITEM)
                        }
                    }

                    "when the number of items is more than the number of elements in prefixItems" - {

                        "when additional items are of valid type" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                BooleanNode.valueOf(true)
                            )

                            "then should return all items read" {
                                val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                                result shouldBeSuccess listOf(FIRST_ITEM, SECOND_ITEM, true)
                            }
                        }

                        "when additional items are of invalid type" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                StringNode(THIRD_ITEM),
                                NumericNode.Integer.valueOf(10)
                            )

                            "then should return all errors" {
                                val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                                result shouldBeFailure listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(BooleanNode.nameOfType),
                                            actual = StringNode.nameOfType
                                        )
                                    ),
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(3),
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(BooleanNode.nameOfType),
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
    }

    internal class EB : AdditionalItemsErrorBuilder,
                        InvalidTypeErrorBuilder {
        override fun additionalItemsError(): ReaderResult.Error = JsonErrors.AdditionalItems

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
