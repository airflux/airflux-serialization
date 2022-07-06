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

package io.github.airflux.dsl.reader.`object`.builder.validator

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.dsl.reader.`object`.builder.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.validator.JsObjectValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsObjectReaderValidationBuilderTest : FreeSpec() {

    companion object {
        private val PROPERTIES = JsObjectProperties(emptyList())
    }

    init {

        "The JsObjectReaderValidationBuilder type" - {

            "when the config is not contained any object validators" - {
                val config = JsObjectReaderConfig.Builder().build()
                val validatorsBuilder = JsObjectReaderValidationBuilder(config)

                "when the before validator was not overridden" - {
                    val validators = validatorsBuilder.build(PROPERTIES)

                    "then there is no validator" {
                        validators.before.shouldBeNull()
                    }
                }

                "when the before validator was overridden" - {
                    val validatorBuilder = BeforeObjectValidatorBuilder()
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                before = validatorBuilder
                            }
                        }
                        .build(PROPERTIES)

                    "then the overridden validator is used" {
                        validators.before shouldBe validatorBuilder.validator
                    }
                }

                "when the after validator was not overridden" - {
                    val validators = validatorsBuilder.build(PROPERTIES)

                    "then there is no validator" {
                        validators.after.shouldBeNull()
                    }
                }

                "when the after validator was overridden" - {
                    val validatorBuilder = AfterObjectValidatorBuilder()
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                after = validatorBuilder
                            }
                        }
                        .build(PROPERTIES)

                    "then the overridden validator is used" {
                        validators.after shouldBe validatorBuilder.validator
                    }
                }
            }

            "when the config contains object validators" - {
                val initBeforeValidatorBuilder = BeforeObjectValidatorBuilder()
                val initAfterValidatorBuilder = AfterObjectValidatorBuilder()
                val config = JsObjectReaderConfig.Builder()
                    .apply {
                        validation {
                            before = initBeforeValidatorBuilder
                            after = initAfterValidatorBuilder
                        }
                    }
                    .build()
                val validatorsBuilder = JsObjectReaderValidationBuilder(config)

                "when the before validator was not overridden" - {
                    val validators = validatorsBuilder.build(PROPERTIES)

                    "then the validator is used from the config" {
                        validators.before shouldBe initBeforeValidatorBuilder.validator
                    }
                }

                "when the before validator was overridden" - {
                    val validatorBuilder = BeforeObjectValidatorBuilder()
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                before = validatorBuilder
                            }
                        }
                        .build(PROPERTIES)

                    "then the overridden validator is used" {
                        validators.before shouldBe validatorBuilder.validator
                    }
                }

                "when the after validator was not overridden" - {
                    val validators = validatorsBuilder.build(PROPERTIES)

                    "then the validator is used from the config" {
                        validators.after shouldBe initAfterValidatorBuilder.validator
                    }
                }

                "when the after validator was overridden" - {
                    val validatorBuilder = AfterObjectValidatorBuilder()
                    val validators = validatorsBuilder
                        .apply {
                            validation {
                                after = validatorBuilder
                            }
                        }
                        .build(PROPERTIES)

                    "then the overridden validator is used" {
                        validators.after shouldBe validatorBuilder.validator
                    }
                }
            }
        }
    }

    internal class BeforeObjectValidatorBuilder : JsObjectValidatorBuilder.Before {
        val validator = Validator()
        override fun build(properties: JsObjectProperties): JsObjectValidator.Before = validator

        internal class Validator : JsObjectValidator.Before {
            override fun validate(
                context: JsReaderContext,
                location: JsLocation,
                properties: JsObjectProperties,
                input: JsObject
            ): JsResult.Failure? = null
        }
    }

    internal class AfterObjectValidatorBuilder : JsObjectValidatorBuilder.After {
        val validator = Validator()
        override fun build(properties: JsObjectProperties): JsObjectValidator.After = validator

        internal class Validator : JsObjectValidator.After {
            override fun validate(
                context: JsReaderContext,
                location: JsLocation,
                properties: JsObjectProperties,
                objectValuesMap: ObjectValuesMap,
                input: JsObject
            ): JsResult.Failure? = null
        }
    }
}
