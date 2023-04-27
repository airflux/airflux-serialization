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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.dsl.reader.array.validator.ArrayValidator
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class MinItemsArrayValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private const val MIN_ITEMS = 2
    }

    init {

        "The array validator MinItems" - {
            val validator: ArrayValidator<EB, Unit, Unit> =
                StdArrayValidator.minItems<EB, Unit, Unit>(MIN_ITEMS).build()

            "when a collection is empty" - {
                val source = ArrayNode()

                "the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, source)

                    val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                    failure.reason shouldBe failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ITEMS, actual = source.size)
                    )
                }
            }

            "when the collection contains a number of elements less than the minimum" - {
                val source = ArrayNode(StringNode("A"))

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, source)

                    val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                    failure.reason shouldBe failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ITEMS, actual = source.size)
                    )
                }
            }

            "when the collection contains a number of elements equal to the minimum" - {
                val source = ArrayNode(StringNode("A"), StringNode("B"))

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, source)
                    result.shouldBeInstanceOf<ValidationResult.Valid>()
                }
            }

            "when the collection contains a number of elements more than the minimum" - {
                val source = ArrayNode(StringNode("A"), StringNode("B"), StringNode("C"))

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, source)
                    result.shouldBeInstanceOf<ValidationResult.Valid>()
                }
            }
        }
    }

    internal class EB : MinItemsArrayValidator.ErrorBuilder {
        override fun minItemsArrayError(expected: Int, actual: Int): ReadingResult.Error =
            JsonErrors.Validation.Arrays.MinItems(expected = expected, actual = actual)
    }
}
