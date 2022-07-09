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

package io.github.airflux.dsl.reader.array.builder.validator

import io.github.airflux.common.DummyAfterArrayValidatorBuilder
import io.github.airflux.common.DummyBeforeArrayValidatorBuilder
import io.github.airflux.dsl.reader.config.JsArrayReaderConfig
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayReaderValidationBuilderTest : FreeSpec() {

    init {

        "The JsArrayReaderValidationBuilder type" - {

            "when the config is not contained any object validators" - {
                val config = JsArrayReaderConfig.Builder().build()
                val validatorsBuilder = JsArrayReaderValidationBuilder<String>(config)

                "when the before validator was not overridden" - {
                    val validators = validatorsBuilder.build()

                    "then there is no validator" {
                        validators.before.shouldBeNull()
                    }
                }

                "when the before validator was overridden" - {
                    val validatorBuilder = DummyBeforeArrayValidatorBuilder(result = null)
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                before = validatorBuilder
                            }
                        }
                        .build()

                    "then the overridden validator is used" {
                        validators.before shouldBe validatorBuilder.validator
                    }
                }

                "when the after validator was not overridden" - {
                    val validators = validatorsBuilder.build()

                    "then there is no validator" {
                        validators.after.shouldBeNull()
                    }
                }

                "when the after validator was overridden" - {
                    val validatorBuilder = DummyAfterArrayValidatorBuilder<String>(result = null)
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                after = validatorBuilder
                            }
                        }
                        .build()

                    "then the overridden validator is used" {
                        validators.after shouldBe validatorBuilder.validator
                    }
                }
            }

            "when the config contains object validators" - {
                val initBeforeValidatorBuilder = DummyBeforeArrayValidatorBuilder(result = null)
                val config = JsArrayReaderConfig.Builder()
                    .apply {
                        validation {
                            before = initBeforeValidatorBuilder
                        }
                    }
                    .build()
                val validatorsBuilder = JsArrayReaderValidationBuilder<String>(config)

                "when the before validator was not overridden" - {
                    val validators = validatorsBuilder.build()

                    "then the validator is used from the config" {
                        validators.before shouldBe initBeforeValidatorBuilder.validator
                    }
                }

                "when the before validator was overridden" - {
                    val validatorBuilder = DummyBeforeArrayValidatorBuilder(result = null)
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                before = validatorBuilder
                            }
                        }
                        .build()

                    "then the overridden validator is used" {
                        validators.before shouldBe validatorBuilder.validator
                    }
                }

                "when the after validator was not overridden" - {
                    val validators = validatorsBuilder.build()

                    "then the validator is used from the config" {
                        validators.after.shouldBeNull()
                    }
                }

                "when the after validator was overridden" - {
                    val validatorBuilder = DummyAfterArrayValidatorBuilder<String>(result = null)
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                after = validatorBuilder
                            }
                        }
                        .build()

                    "then the overridden validator is used" {
                        validators.after shouldBe validatorBuilder.validator
                    }
                }
            }
        }
    }
}
