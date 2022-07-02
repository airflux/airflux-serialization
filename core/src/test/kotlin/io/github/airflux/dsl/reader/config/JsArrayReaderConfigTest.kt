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

import io.github.airflux.std.validator.array.ArrayValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

internal class JsArrayReaderConfigTest : FreeSpec() {

    init {

        "when any validator is not registered in the configuration builder" - {
            val config: JsArrayReaderConfig = arrayReaderConfig({})

            "then validator should miss in the configuration" {
                config.validation.before.shouldBeNull()
            }
        }

        "when some validator is registered in the configuration builder" - {
            val config: JsArrayReaderConfig = arrayReaderConfig {
                this.validation {
                    this.before = ArrayValidator.isNotEmpty
                }
            }

            "then validator should present in the configuration" {
                config.validation.before.shouldNotBeNull()
            }
        }
    }
}
