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

package io.github.airflux.serialization.dsl.reader.`object`.builder.validator

import io.github.airflux.serialization.common.DummyObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.config.ObjectReaderConfig
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperties
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should

internal class ObjectReaderValidatorsBuilderInstanceTest : FreeSpec() {

    companion object {
        private val PROPERTIES = ObjectProperties(emptyList())
    }

    init {

        "The ObjectReaderValidatorsBuilderInstance type" - {

            "when the config is not contained the object validator" - {
                val config = ObjectReaderConfig.Builder().build()
                val validationBuilder = ObjectReaderValidatorsBuilderInstance(config)

                "when the validator was not overridden" - {
                    val validators = validationBuilder.build(PROPERTIES)

                    "then there is no validator" {
                        validators should beEmpty()
                    }
                }

                "when the validator was overridden" - {
                    val someValidatorBuilder = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = null
                    )
                    val validators = validationBuilder
                        .apply {
                            validation {
                                +someValidatorBuilder
                            }
                        }
                        .build(PROPERTIES)

                    "then the overridden validator is used" {
                        validators shouldContainExactly listOf(someValidatorBuilder.validator)
                    }
                }
            }

            "when the config contains the object validator" - {
                val initValidatorBuilder = DummyObjectValidatorBuilder(
                    key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                    result = null
                )
                val config = ObjectReaderConfig.Builder()
                    .apply {
                        validation {
                            +initValidatorBuilder
                        }
                    }
                    .build()
                val validationBuilder = ObjectReaderValidatorsBuilderInstance(config)

                "when the validator was not overridden" - {
                    val validators = validationBuilder.build(PROPERTIES)

                    "then the validator is used from the config" {
                        validators shouldContainExactly listOf(initValidatorBuilder.validator)
                    }
                }

                "when the validator was overridden" - {
                    val someValidatorBuilder = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = null
                    )
                    val validators = validationBuilder
                        .apply {
                            validation {
                                +someValidatorBuilder
                            }
                        }
                        .build(PROPERTIES)

                    "then the overridden validator is used" {
                        validators shouldContainExactly listOf(
                            initValidatorBuilder.validator,
                            someValidatorBuilder.validator
                        )
                    }
                }
            }
        }
    }
}
