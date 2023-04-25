/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.validation

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ValidationResultTest : FreeSpec() {

    init {
        "The ValidationResult type" - {

            "when value is valid" - {
                val value = valid()

                "then the fold method should execute the ifValid code block" {
                    var result = 0
                    value.fold(
                        ifValid = { result = 10 },
                        ifInvalid = { result = 20 }
                    )
                    result shouldBe 10
                }

                "then the ifInvalid method should not do any action" {
                    var result = false
                    value.ifInvalid { result = true }
                    result shouldBe false
                }
            }

            "when value is invalid" - {
                val value = invalid(location = Location.empty, error = JsonErrors.PathMissing)

                "then the fold method should execute the ifInvalid code block" {
                    var result = 0
                    value.fold(
                        ifValid = { result = 10 },
                        ifInvalid = { result = 20 }
                    )
                    result shouldBe 20
                }

                "then the ifInvalid method should do some action" {
                    var result = false
                    value.ifInvalid { result = true }
                    result shouldBe true
                }
            }
        }
    }
}
