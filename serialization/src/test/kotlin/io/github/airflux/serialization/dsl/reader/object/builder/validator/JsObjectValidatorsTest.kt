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

import io.github.airflux.serialization.common.DummyObjectValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsObjectValidatorsTest : FreeSpec() {

    init {

        "The JsObjectValidators type" - {

            "when some validators was passed during initialize" - {
                val firstValidatorBuilder: ObjectValidator = DummyObjectValidator(result = null)
                val secondValidatorBuilder: ObjectValidator = DummyObjectValidator(result = null)
                val validators = JsObjectValidators(listOf(firstValidatorBuilder, secondValidatorBuilder))

                "then the validators container should contain the passed validators" {
                    validators shouldContainExactly listOf(firstValidatorBuilder, secondValidatorBuilder)
                }
            }

            "when any validators was not passed during initialize" - {
                val validators = JsObjectValidators(emptyList())

                "then should be returns empty instance" {
                    validators shouldBe JsObjectValidators(emptyList())
                }
            }
        }
    }
}
