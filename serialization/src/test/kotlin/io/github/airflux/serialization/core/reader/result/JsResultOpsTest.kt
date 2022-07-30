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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.validator.JsValidator
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsResultOpsTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The extension-function the filter" - {
            val isNotBlank = JsPredicate<String> { _, _, value -> value.isNotBlank() }

            "when result is success" - {

                "when the value satisfies the predicate" - {
                    val result: JsResult<String> = JsResult.Success(location = LOCATION, value = "  ")

                    "then filter should return null" {
                        val filtered = result.filter(CONTEXT, isNotBlank)
                        filtered shouldBe JsResult.Success(location = LOCATION, value = null)
                    }
                }

                "when the value does not satisfy the predicate and is not the null" - {
                    val result: JsResult<String> =
                        JsResult.Success(location = LOCATION, value = "user")

                    "then filter should return the original value" {
                        val filtered = result.filter(CONTEXT, isNotBlank)
                        filtered shouldBe result
                    }
                }

                "when the value does not satisfy the predicate and is the null" - {
                    val result: JsResult<String?> = JsResult.Success(location = LOCATION, value = null)

                    "then filter should return the original value" {
                        val filtered = result.filter(CONTEXT, isNotBlank)
                        filtered shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: JsResult<String> = JsResult.Failure(
                    location = LOCATION,
                    error = JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                )

                "then filter should return the original value" {
                    val filtered = result.filter(CONTEXT, isNotBlank)
                    filtered shouldBe result
                }
            }
        }

        "The extension-function the validation" - {
            val isNotEmpty = JsValidator<String> { _, location, value ->
                if (value.isNotEmpty()) null else JsResult.Failure(location, JsonErrors.Validation.Strings.IsEmpty)
            }

            "when result is success" - {

                "when the value contains an invalid value" - {
                    val result: JsResult<String> = JsResult.Success(location = LOCATION, value = "")

                    "then validator should return an error" {
                        val validated = result.validation(CONTEXT, isNotEmpty)

                        validated shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when the value contains a valid value" - {
                    val result: JsResult<String> = JsResult.Success(location = LOCATION, value = "user")

                    "then validator should return the original value" {
                        val validated = result.validation(CONTEXT, isNotEmpty)
                        validated shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: JsResult<String> = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then validator should return the original value" {
                    val validated = result.validation(CONTEXT, isNotEmpty)
                    validated shouldBe result
                }
            }
        }
    }
}
