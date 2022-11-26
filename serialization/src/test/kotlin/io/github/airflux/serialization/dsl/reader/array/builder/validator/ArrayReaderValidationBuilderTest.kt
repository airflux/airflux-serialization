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

package io.github.airflux.serialization.dsl.reader.array.builder.validator

import io.github.airflux.serialization.common.DummyArrayValidatorBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

internal class ArrayReaderValidationBuilderTest : FreeSpec() {

    companion object {
        private val firstValidatorBuilder = DummyArrayValidatorBuilder<Unit, Unit>(
            key = DummyArrayValidatorBuilder.key<Unit, Unit, DummyArrayValidatorBuilder<Unit, Unit>>(),
            result = null
        )
        private val secondValidatorBuilder = DummyArrayValidatorBuilder<Unit, Unit>(
            key = DummyArrayValidatorBuilder.key<Unit, Unit, DummyArrayValidatorBuilder<Unit, Unit>>(),
            result = null
        )
    }

    init {

        "The ArrayReaderValidation#Builder type" - {

            "when the builder is created" - {

                "then the builder should return the empty collection" {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>()
                    val validatorBuilders = builder.build()
                    validatorBuilders.shouldBeEmpty()
                }
            }

            "when the builder does not contain any item" - {

                "when some item is added" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        +firstValidatorBuilder
                        +secondValidatorBuilder
                    }

                    "then the builder should return the collection with added items" {
                        val validatorBuilders = builder.build()
                        validatorBuilders shouldContainExactly listOf(
                            firstValidatorBuilder,
                            secondValidatorBuilder
                        )
                    }
                }

                "when a collection of items is added" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        +listOf(firstValidatorBuilder, secondValidatorBuilder)
                    }

                    "then the builder should return the collection with added items" {
                        val validatorBuilders = builder.build()
                        validatorBuilders shouldContainExactly listOf(
                            firstValidatorBuilder,
                            secondValidatorBuilder
                        )
                    }
                }

                "when some item is removed" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        -firstValidatorBuilder
                    }

                    "then the builder should return the empty collection" {
                        val validatorBuilders = builder.build()
                        validatorBuilders.shouldBeEmpty()
                    }
                }

                "when a collection of items is removed" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        -listOf(firstValidatorBuilder, secondValidatorBuilder)
                    }

                    "then the builder should return the empty collection" {
                        val validatorBuilders = builder.build()
                        validatorBuilders.shouldBeEmpty()
                    }
                }
            }

            "when the builder contains some item" - {

                "when some unknown item is removed" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        +firstValidatorBuilder
                        -secondValidatorBuilder
                    }

                    "then the builder should return items that were before remove" {
                        val validatorBuilders = builder.build()
                        validatorBuilders shouldContainExactly listOf(firstValidatorBuilder)
                    }
                }

                "when a famous item is removed" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        +firstValidatorBuilder
                        +secondValidatorBuilder
                        -firstValidatorBuilder
                    }

                    "then the builder should return the remaining items" {
                        val validatorBuilders = builder.build()
                        validatorBuilders shouldContainExactly listOf(secondValidatorBuilder)
                    }
                }

                "when a collection of unknown items is removed" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        +firstValidatorBuilder
                        -listOf(secondValidatorBuilder)
                    }

                    "then the builder should return items that were before remove" {
                        val validatorBuilders = builder.build()
                        validatorBuilders shouldContainExactly listOf(firstValidatorBuilder)
                    }
                }

                "when a collection of items is removed" - {
                    val builder = ArrayReaderValidation.Builder<Unit, Unit>().apply {
                        +listOf(firstValidatorBuilder, secondValidatorBuilder)
                        -listOf(firstValidatorBuilder)
                    }

                    "then the builder should return the remaining items" {
                        val validatorBuilders = builder.build()
                        validatorBuilders shouldContainExactly listOf(secondValidatorBuilder)
                    }
                }
            }
        }
    }
}
