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

import io.github.airflux.common.DummyObjectValidatorBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should

internal class JsObjectReaderConfigTest : FreeSpec() {

    companion object {
        val validator = DummyObjectValidatorBuilder(
            key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
            result = null
        )
    }

    init {

        "when any parameters are not set in the builder" - {
            val config: JsObjectReaderConfig = objectReaderConfig {}

            "then the validator should be missing" {
                config.validation should beEmpty()
            }
        }

        "when some validators were set in the builder" - {
            val config: JsObjectReaderConfig = objectReaderConfig {
                validation {
                    +validator
                }
            }

            "then the validator should be present" {
                config.validation shouldContainExactly listOf(validator)
            }
        }
    }
}
