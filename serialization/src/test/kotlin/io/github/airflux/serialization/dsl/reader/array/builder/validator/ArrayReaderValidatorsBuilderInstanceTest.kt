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
import io.github.airflux.serialization.dsl.reader.config.ArrayReaderConfig
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should

internal class ArrayReaderValidatorsBuilderInstanceTest : FreeSpec() {

    init {

        "The ArrayReaderValidatorsBuilderInstance type" - {

            "when the config is not contained any object validators" - {
                val config = ArrayReaderConfig.Builder().build()
                val validatorsBuilder = ArrayReaderValidatorsBuilderInstance(config)

                "when the validator was not overridden" - {
                    val validators = validatorsBuilder.build()

                    "then there is no validator" {
                        validators should beEmpty()
                    }
                }

                "when the validator was overridden" - {
                    val validatorBuilder = DummyArrayValidatorBuilder(
                        key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                        result = null
                    )
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                +validatorBuilder
                            }
                        }
                        .build()

                    "then the overridden validator is used" {
                        validators shouldContainExactly listOf(validatorBuilder.validator)
                    }
                }
            }

            "when the config contains object validators" - {
                val initValidatorBuilder = DummyArrayValidatorBuilder(
                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                    result = null
                )
                val config = ArrayReaderConfig.Builder()
                    .apply {
                        validation {
                            +initValidatorBuilder
                        }
                    }
                    .build()
                val validatorsBuilder = ArrayReaderValidatorsBuilderInstance(config)

                "when the validator was not overridden" - {
                    val validators = validatorsBuilder.build()

                    "then the validator is used from the config" {
                        validators shouldContainExactly listOf(initValidatorBuilder.validator)
                    }
                }

                "when the validator was overridden" - {
                    val validatorBuilder = DummyArrayValidatorBuilder(
                        key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                        result = null
                    )
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                +validatorBuilder
                            }
                        }
                        .build()

                    "then the overridden validator is used" {
                        validators shouldContainExactly listOf(
                            initValidatorBuilder.validator,
                            validatorBuilder.validator
                        )
                    }
                }
            }
        }
    }
}
