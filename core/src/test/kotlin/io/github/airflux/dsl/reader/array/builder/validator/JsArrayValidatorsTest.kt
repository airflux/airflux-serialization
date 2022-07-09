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
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayValidatorsTest : FreeSpec() {

    init {

        "The JsArrayValidators type" - {

            "when the builder was not initialized with any values" - {
                val builder = JsArrayValidators.Builder<String>()

                "when any object validators do not set in the builder" - {
                    val validators = builder.build()

                    "then the container of validators are not contained the before validator" {
                        validators.before.shouldBeNull()
                    }

                    "then the container of validators are not contained the after validator" {
                        validators.after.shouldBeNull()
                    }
                }

                "when object validators do set in the builder" - {
                    val beforeValidatorBuilder = DummyBeforeArrayValidatorBuilder(result = null)
                    val afterValidatorBuilder = DummyAfterArrayValidatorBuilder<String>(result = null)
                    val validators = builder.apply {
                        before = beforeValidatorBuilder
                        after = afterValidatorBuilder
                    }.build()

                    "then the container of validators contains the before validator that was set in the builder" {
                        validators.before shouldBe beforeValidatorBuilder.validator
                    }

                    "then the container of validators contains the after validator that was set in the builder" {
                        validators.after shouldBe afterValidatorBuilder.validator
                    }
                }
            }

            "when the builder was initialized with validators" - {
                val initBeforeValidatorBuilder = DummyBeforeArrayValidatorBuilder(result = null)
                val initAfterValidatorBuilder = DummyAfterArrayValidatorBuilder<String>(result = null)
                val builder =
                    JsArrayValidators.Builder(before = initBeforeValidatorBuilder, after = initAfterValidatorBuilder)

                "when any object validators do not set in the builder" - {
                    val validators = builder.build()

                    "then the container of validators contains the before validator that was passed when the builder was initialized" {
                        validators.before shouldBe initBeforeValidatorBuilder.validator
                    }

                    "then the container of validators contains the after validator that was passed when the builder was initialized" {
                        validators.after shouldBe initAfterValidatorBuilder.validator
                    }
                }

                "when object validators do set in the builder" - {
                    val beforeValidatorBuilder = DummyBeforeArrayValidatorBuilder(result = null)
                    val afterValidatorBuilder = DummyAfterArrayValidatorBuilder<String>(result = null)
                    val validators = builder.apply {
                        before = beforeValidatorBuilder
                        after = afterValidatorBuilder
                    }.build()

                    "then the container of validators contains the before validator that was set in the builder" {
                        validators.before shouldBe beforeValidatorBuilder.validator
                    }

                    "then the container of validators contains the after validator that was set in the builder" {
                        validators.after shouldBe afterValidatorBuilder.validator
                    }
                }
            }
        }
    }
}
