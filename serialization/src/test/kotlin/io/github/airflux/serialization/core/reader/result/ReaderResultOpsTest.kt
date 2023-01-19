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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReaderResultOpsTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
    }

    init {

        "The extension-function the filter" - {
            val isNotBlank = ReaderPredicate<EB, Unit, Unit, String> { _, _, _, value -> value.isNotBlank() }

            "when result is success" - {

                "when the value satisfies the predicate" - {
                    val result: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = "  ")

                    "then filter should return null" {
                        val filtered = result.filter(ENV, CONTEXT, isNotBlank)
                        filtered shouldBe ReaderResult.Success(location = LOCATION, value = null)
                    }
                }

                "when the value does not satisfy the predicate and is not the null" - {
                    val result: ReaderResult<String> =
                        ReaderResult.Success(location = LOCATION, value = "user")

                    "then filter should return the original value" {
                        val filtered = result.filter(ENV, CONTEXT, isNotBlank)
                        filtered shouldBe result
                    }
                }

                "when the value does not satisfy the predicate and is the null" - {
                    val result: ReaderResult<String?> = ReaderResult.Success(location = LOCATION, value = null)

                    "then filter should return the original value" {
                        val filtered = result.filter(ENV, CONTEXT, isNotBlank)
                        filtered shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: ReaderResult<String> = ReaderResult.Failure(
                    location = LOCATION,
                    error = JsonErrors.InvalidType(
                        expected = listOf(StringNode.nameOfType),
                        actual = BooleanNode.nameOfType
                    )
                )

                "then filter should return the original value" {
                    val filtered = result.filter(ENV, CONTEXT, isNotBlank)
                    filtered shouldBe result
                }
            }
        }

        "The extension-function the validation" - {
            val isNotEmpty = Validator<EB, Unit, Unit, String> { _, _, location, value ->
                if (value.isNotEmpty())
                    null
                else
                    ReaderResult.Failure(location, JsonErrors.Validation.Strings.IsEmpty)
            }

            "when result is success" - {

                "when the value contains an invalid value" - {
                    val result: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = "")

                    "then validator should return an error" {
                        val validated = result.validation(ENV, CONTEXT, isNotEmpty)

                        validated shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when the value contains a valid value" - {
                    val result: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = "user")

                    "then validator should return the original value" {
                        val validated = result.validation(ENV, CONTEXT, isNotEmpty)
                        validated shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then validator should return the original value" {
                    val validated = result.validation(ENV, CONTEXT, isNotEmpty)
                    validated shouldBe result
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
