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

package io.github.airflux.core.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.github.airflux.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsReaderOpsTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(
            listOf(
                PathMissingErrorBuilder(builder = { JsonErrors.PathMissing }),
                InvalidTypeErrorBuilder(builder = JsonErrors::InvalidType)
            )
        )
        private val LOCATION = JsLocation.empty
        private const val VALUE = "ABC"
    }

    init {
        val reader = StringReader

        "The extension-function the filter" - {
            val isNotBlank = JsPredicate<String> { _, _, value -> value.isNotBlank() }

            "when the value satisfies the predicate" - {
                val json: JsValue = JsString("  ")

                "then filter should return the null value" {
                    val filtered = reader.filter(isNotBlank).read(CONTEXT, LOCATION, json)
                    filtered shouldBe JsResult.Success(location = LOCATION, value = null)
                }
            }

            "when the value does not satisfy the predicate" - {
                val json: JsValue = JsString(VALUE)

                "then filter should return the original value" {
                    val filtered = reader.filter(isNotBlank).read(CONTEXT, LOCATION, json)
                    filtered shouldBe JsResult.Success(location = LOCATION, value = VALUE)
                }
            }
        }

        "The extension-function the validation" - {
            val isNotEmpty = JsValidator<String> { _, location, value ->
                if (value.isNotEmpty()) null else JsResult.Failure(location, JsonErrors.Validation.Strings.IsEmpty)
            }

            "when the value is invalid" - {
                val json: JsValue = JsString("")

                "then validator should return the failure" {
                    val validated = reader.validation(isNotEmpty).read(CONTEXT, LOCATION, json)

                    validated shouldBe JsResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.IsEmpty
                    )
                }
            }

            "when the value is valid" - {
                val json: JsValue = JsString(VALUE)

                "then validator should return the success" {
                    val validated = reader.validation(isNotEmpty).read(CONTEXT, LOCATION, json)
                    validated shouldBe JsResult.Success(location = LOCATION, value = VALUE)
                }
            }
        }
    }
}
