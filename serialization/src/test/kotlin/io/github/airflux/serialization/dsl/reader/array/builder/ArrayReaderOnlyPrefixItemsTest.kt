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

package io.github.airflux.serialization.dsl.reader.array.builder

import io.github.airflux.serialization.common.DummyArrayValidatorBuilder.Companion.minItems
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.prefixItems
import io.github.airflux.serialization.std.reader.stringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class ArrayReaderOnlyPrefixItemsTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "first"
        private const val SECOND_ITEM = "second"
        private const val THIRD_ITEM = "third"
        private const val FOUR_ITEM = "four"

        private val LOCATION = Location.empty
        private const val MIN_ITEMS = 2

        private val StringReader = stringReader<EB, CTX>()
    }

    init {

        "The ArrayReader type" - {

            "when a reader was created for prefixItems" - {

                "when the additional items do not cause an error" - {
                    val reader: Reader<EB, CTX, List<String>> = arrayReader {
                        validation(
                            minItems(
                                expected = MIN_ITEMS,
                                error = { expected, actual -> JsonErrors.Validation.Arrays.MinItems(expected, actual) }
                            )
                        )
                        returns(
                            prefixItems = prefixItems(
                                nonNullable(StringReader),
                                nonNullable(StringReader),
                                nonNullable(StringReader)
                            ),
                            items = true
                        )
                    }

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                        "when source is not the array type" - {
                            val source = StringNode("")

                            "then the reader should return the invalid type error" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(ArrayNode.nameOfType),
                                            actual = StringNode.nameOfType
                                        )
                                    )
                                )
                            }
                        }

                        "when the number of items is less than the number of elements in prefixItems" - {
                            val source = ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                            }
                        }

                        "when the number of items is equal to the number of elements in prefixItems" - {
                            val source =
                                ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM), StringNode(THIRD_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM)
                            }
                        }

                        "when the number of items is more than the number of elements in prefixItems" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                StringNode(THIRD_ITEM),
                                StringNode(FOUR_ITEM)
                            )

                            "then should only return items from prefixItems" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM)
                            }
                        }

                        "when error occur of validation the array" - {
                            val source = ArrayNode<StringNode>()

                            "then the reader should return it error" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.Validation.Arrays.MinItems(
                                            expected = MIN_ITEMS,
                                            actual = source.size
                                        )
                                    )
                                )
                            }
                        }

                        "when error occur of validation the array and reading items" - {
                            val source = ArrayNode<ValueNode>(BooleanNode.valueOf(true))

                            "then the reader should return validation error" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.Validation.Arrays.MinItems(
                                            expected = MIN_ITEMS,
                                            actual = source.size
                                        )
                                    )
                                )
                            }
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                        "when source is not the array type" - {
                            val source = StringNode("")

                            "then the reader should return the invalid type error" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(ArrayNode.nameOfType),
                                            actual = StringNode.nameOfType
                                        )
                                    )
                                )
                            }
                        }

                        "when the number of items is less than the number of elements in prefixItems" - {
                            val source = ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                            }
                        }

                        "when the number of items is equal to the number of elements in prefixItems" - {
                            val source =
                                ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM), StringNode(THIRD_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM)
                            }
                        }

                        "when the number of items is more than the number of elements in prefixItems" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                StringNode(THIRD_ITEM),
                                StringNode(FOUR_ITEM)
                            )

                            "then should only return items from prefixItems" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM)
                            }
                        }

                        "when error occur of validation the array" - {
                            val source = ArrayNode<StringNode>()

                            "then the reader should return it error" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.Validation.Arrays.MinItems(
                                            expected = MIN_ITEMS,
                                            actual = source.size
                                        )
                                    )
                                )
                            }
                        }

                        "when error occur of validation the array and reading items" - {
                            val source = ArrayNode<ValueNode>(BooleanNode.valueOf(true))

                            "then the reader should return all errors" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.Validation.Arrays.MinItems(
                                            expected = MIN_ITEMS,
                                            actual = source.size
                                        )
                                    ),
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(0),
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(StringNode.nameOfType),
                                            actual = BooleanNode.nameOfType
                                        )
                                    )
                                )
                            }
                        }
                    }
                }

                "when the additional items do cause an error" - {
                    val reader: Reader<EB, CTX, List<String>> = arrayReader {
                        returns(
                            prefixItems = prefixItems(
                                nonNullable(StringReader),
                                nonNullable(StringReader)
                            ),
                            items = false
                        )
                    }

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                        "when source is not the array type" - {
                            val source = StringNode("")

                            "then the reader should return the invalid type error" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(ArrayNode.nameOfType),
                                            actual = StringNode.nameOfType
                                        )
                                    )
                                )
                            }
                        }

                        "when the number of items is less than the number of elements in prefixItems" - {
                            val source = ArrayNode(StringNode(FIRST_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM)
                            }
                        }

                        "when the number of items is equal to the number of elements in prefixItems" - {
                            val source = ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                            }
                        }

                        "when the number of items is more than the number of elements in prefixItems" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                StringNode(THIRD_ITEM),
                                StringNode(FOUR_ITEM)
                            )

                            "then should return an error" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.AdditionalItems
                                    )
                                )
                            }
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                        "when source is not the array type" - {
                            val source = StringNode("")

                            "then the reader should return the invalid type error" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION,
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(ArrayNode.nameOfType),
                                            actual = StringNode.nameOfType
                                        )
                                    )
                                )
                            }
                        }

                        "when the number of items is less than the number of elements in prefixItems" - {
                            val source = ArrayNode(StringNode(FIRST_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM)
                            }
                        }

                        "when the number of items is equal to the number of elements in prefixItems" - {
                            val source = ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))

                            "then should return all elements read" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Success
                                result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                            }
                        }

                        "when the number of items is more than the number of elements in prefixItems" - {
                            val source = ArrayNode(
                                StringNode(FIRST_ITEM),
                                StringNode(SECOND_ITEM),
                                StringNode(THIRD_ITEM),
                                StringNode(FOUR_ITEM)
                            )

                            "then should return all errors" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result as ReaderResult.Failure
                                result.causes shouldContainExactly listOf(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.AdditionalItems
                                    ),
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(3),
                                        error = JsonErrors.AdditionalItems
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

    internal class CTX(override val failFast: Boolean) : FailFastOption
}
