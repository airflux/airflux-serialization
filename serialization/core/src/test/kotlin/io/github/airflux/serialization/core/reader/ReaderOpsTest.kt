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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.DummyReaderPredicate
import io.github.airflux.serialization.core.common.DummyValidator
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReaderOpsTest : FreeSpec() {

    companion object {
        private val ENV: ReaderEnv<Unit, Unit> = ReaderEnv(Unit, Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private const val VALUE = "ABC"
        private val JSON_VALUE: ValueNode = StringNode(VALUE)
    }

    init {

        "The extension-function the filter" - {

            "when an original reader returns a result as a success" - {
                val reader: Reader<Unit, Unit, Unit, String> = DummyReader(
                    result = ReaderResult.Success(location = LOCATION, value = VALUE)
                )

                "when the value satisfies the predicate" - {
                    val predicate: ReaderPredicate<Unit, Unit, Unit, String> = DummyReaderPredicate(result = false)

                    "then filter should return the null value" {
                        val filtered = reader.filter(predicate).read(ENV, CONTEXT, LOCATION, JSON_VALUE)
                        filtered shouldBe ReaderResult.Success(location = LOCATION, value = null)
                    }
                }

                "when the value does not satisfy the predicate" - {
                    val predicate: ReaderPredicate<Unit, Unit, Unit, String> = DummyReaderPredicate(result = true)

                    "then filter should return the original value" {
                        val filtered = reader.filter(predicate).read(ENV, CONTEXT, LOCATION, JSON_VALUE)
                        filtered shouldBe ReaderResult.Success(location = LOCATION, value = VALUE)
                    }
                }
            }

            "when an original reader returns a result as a failure" - {
                val reader: Reader<Unit, Unit, Unit, String> = DummyReader(
                    result = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                )

                "then filtering does not execute and the original result should be returned" {
                    val predicate: ReaderPredicate<Unit, Unit, Unit, String> = DummyReaderPredicate(result = false)
                    val validated = reader.filter(predicate).read(ENV, CONTEXT, LOCATION, JSON_VALUE)
                    validated shouldBe ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                }
            }
        }

        "The extension-function the validation" - {

            "when an original reader returns a result as a success" - {
                val reader: Reader<Unit, Unit, Unit, String> = DummyReader(
                    result = ReaderResult.Success(location = LOCATION, value = VALUE)
                )

                "when validation is a success" - {
                    val validator: Validator<Unit, Unit, Unit, String> = DummyValidator(result = null)

                    "then should return the original result" {
                        val validated = reader.validation(validator).read(ENV, CONTEXT, LOCATION, JSON_VALUE)
                        validated shouldBe ReaderResult.Success(location = LOCATION, value = VALUE)
                    }
                }

                "when validation is a failure" - {
                    val validator: Validator<Unit, Unit, Unit, String> = DummyValidator(
                        result = ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    )

                    "then should return the result of a validation" {
                        val validated = reader.validation(validator).read(ENV, CONTEXT, LOCATION, JSON_VALUE)

                        validated shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }
            }

            "when an original reader returns a result as a failure" - {
                val reader: Reader<Unit, Unit, Unit, String> = DummyReader(
                    result = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                )

                "then validation does not execute and the original result should be returned" {
                    val validator: Validator<Unit, Unit, Unit, String> = DummyValidator(
                        result = ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    )
                    val validated = reader.validation(validator).read(ENV, CONTEXT, LOCATION, JSON_VALUE)
                    validated shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.PathMissing
                    )
                }
            }
        }
    }
}