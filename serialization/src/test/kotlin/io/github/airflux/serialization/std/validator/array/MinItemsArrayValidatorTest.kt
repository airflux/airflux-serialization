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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MinItemsArrayValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION = Location.empty
        private const val MIN_ITEMS = 2
    }

    init {

        "The array validator MinItems" - {
            val validator = StdArrayValidator.minItems(MIN_ITEMS).build()

            "when the reader context does not contain the error builder" - {
                val context = ReaderContext()

                "when the test condition is false" {
                    val source: ArrayNode<StringNode> = ArrayNode(StringNode("A"))

                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, source)
                    }
                    exception.message shouldBe "The error builder '${MinItemsArrayValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = ReaderContext(
                    MinItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MinItems)
                )

                "when a collection is empty" - {
                    val source: ArrayNode<StringNode> = ArrayNode()

                    "the validator should return an error" {
                        val failure = validator.validate(context, LOCATION, source)

                        failure.shouldNotBeNull()
                        failure shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ITEMS, actual = source.size)
                        )
                    }
                }

                "when the collection contains a number of elements less than the minimum" - {
                    val source: ArrayNode<StringNode> = ArrayNode(StringNode("A"))

                    "then the validator should return an error" {
                        val failure = validator.validate(context, LOCATION, source)

                        failure.shouldNotBeNull()
                        failure shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ITEMS, actual = source.size)
                        )
                    }
                }

                "when the collection contains a number of elements equal to the minimum" - {
                    val source: ArrayNode<StringNode> = ArrayNode(StringNode("A"), StringNode("B"))

                    "then the validator should do not return any errors" {
                        val failure = validator.validate(context, LOCATION, source)
                        failure.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements more than the minimum" - {
                    val source: ArrayNode<StringNode> = ArrayNode(StringNode("A"), StringNode("B"), StringNode("C"))

                    "then the validator should do not return any errors" {
                        val failure = validator.validate(context, LOCATION, source)
                        failure.shouldBeNull()
                    }
                }
            }
        }
    }
}
