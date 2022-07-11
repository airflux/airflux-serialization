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

import io.github.airflux.common.DummyObjectValidatorBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should

internal class JsObjectValidationTest : FreeSpec() {

    init {

        "The JsObjectValidation type" - {

            "when the builder is empty" - {
                val builder = JsObjectValidation.Builder()

                "when some validator builder was add" - {
                    val validator = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = null
                    )
                    val builderWithItem = builder.apply {
                        +validator
                    }

                    "then the validation object should contain added validator" {
                        val validation = builderWithItem.build()
                        validation shouldContainExactly listOf(validator)
                    }

                    "when the validator was removed" - {
                        builderWithItem.apply {
                            -validator
                        }

                        "then the validation object should be is empty" {
                            val validation = builderWithItem.build()
                            validation should beEmpty()
                        }
                    }
                }
            }

            "when the builder is not empty" - {

                "when any validator builder was not add" - {
                    val initValidator = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = null
                    )
                    val builder = JsObjectValidation.Builder(listOf(initValidator))

                    "then the validation object should contain only validator passed during initialization" {
                        val validation = builder.build()
                        validation shouldContainExactly listOf(initValidator)
                    }
                }

                "when some validator builder was add" - {
                    val initValidator = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = null
                    )
                    val builder = JsObjectValidation.Builder(listOf(initValidator))

                    val validator = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = null
                    )
                    val builderWithItem = builder.apply {
                        +validator
                    }

                    "then the validation object should contain added validator" {
                        val validation = builderWithItem.build()
                        validation shouldContainExactly listOf(initValidator, validator)
                    }
                }

                "when the validator builder with same key was add" - {
                    val key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>()

                    val initValidator = DummyObjectValidatorBuilder(key = key, result = null)
                    val builder = JsObjectValidation.Builder(listOf(initValidator))

                    val validator = DummyObjectValidatorBuilder(key = key, result = null)
                    val builderWithItem = builder.apply {
                        +validator
                    }

                    "then the validation object should contain last added validator" {
                        val validation = builderWithItem.build()
                        validation shouldContainExactly listOf(validator)
                    }
                }
            }
        }
    }
}
