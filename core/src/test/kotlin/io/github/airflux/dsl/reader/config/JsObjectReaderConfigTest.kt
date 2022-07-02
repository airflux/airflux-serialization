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

package io.github.airflux.dsl.reader.config

import io.github.airflux.std.validator.`object`.ObjectValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsObjectReaderConfigTest : FreeSpec() {

    init {

        "when any parameters are not set in the builder" - {
            val config: JsObjectReaderConfig = objectReaderConfig({})

            "then value of the checkUniquePropertyPath parameter should be false" {
                config.checkUniquePropertyPath shouldBe false
            }

            "then the validator before should be missing" {
                config.validation.before.shouldBeNull()
            }

            "then the validator after should be missing" {
                config.validation.after.shouldBeNull()
            }
        }

        "when the checkUniquePropertyPath parameter was set in the builder" - {
            val config: JsObjectReaderConfig = objectReaderConfig {
                checkUniquePropertyPath = true
            }

            "then value of the checkUniquePropertyPath parameter should be true" {
                config.checkUniquePropertyPath shouldBe true
            }

            "then the validator before should be missing" {
                config.validation.before.shouldBeNull()
            }

            "then the validator after should be missing" {
                config.validation.after.shouldBeNull()
            }
        }

        "when some validators were set in the builder" - {
            val config: JsObjectReaderConfig = objectReaderConfig {
                validation {
                    before = ObjectValidator.additionalProperties
                    after = ObjectValidator.isNotEmpty
                }
            }

            "then a value of the checkUniquePropertyPath parameter should be false" {
                config.checkUniquePropertyPath shouldBe false
            }

            "then the validator before should be present" {
                config.validation.before shouldBe ObjectValidator.additionalProperties
            }

            "then the validator after should be present" {
                config.validation.after shouldBe ObjectValidator.isNotEmpty
            }
        }
    }
}
