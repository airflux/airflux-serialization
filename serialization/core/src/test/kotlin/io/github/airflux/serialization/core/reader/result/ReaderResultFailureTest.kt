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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class ReaderResultFailureTest : FreeSpec() {

    companion object {
        private val LOCATION = Location.empty
    }

    init {

        "A ReaderResult#Failure type" - {

            "constructor(JsLocation, JsError)" {
                val failure = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                failure.causes shouldContainExactly listOf(
                    ReaderResult.Failure.Cause(
                        location = LOCATION,
                        errors = ReaderResult.Errors(JsonErrors.PathMissing)
                    )
                )
            }

            "constructor(JsLocation, ReaderResult#Errors)" {
                val errors = ReaderResult.Errors(JsonErrors.PathMissing)

                val failure = ReaderResult.Failure(location = LOCATION, errors = errors)

                failure.causes shouldContainExactly listOf(
                    ReaderResult.Failure.Cause(location = LOCATION, errors = errors)
                )
            }

            "the function ReaderResult#Failure#plus " {
                val firstFailure =
                    ReaderResult.Failure(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing))
                val secondFailure = ReaderResult.Failure(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
                        JsonErrors.InvalidType(
                            expected = listOf(StringNode.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
                    )
                )

                val failure = firstFailure + secondFailure

                failure.causes shouldContainExactly listOf(
                    ReaderResult.Failure.Cause(
                        location = LOCATION,
                        errors = ReaderResult.Errors(JsonErrors.PathMissing)
                    ),
                    ReaderResult.Failure.Cause(
                        location = LOCATION,
                        errors = ReaderResult.Errors(
                            JsonErrors.InvalidType(
                                expected = listOf(StringNode.nameOfType),
                                actual = BooleanNode.nameOfType
                            )
                        )
                    )
                )
            }

            "should comply with equals() and hashCode() contract" {
                ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing).shouldBeEqualsContract(
                    y = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    z = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    others = listOf(
                        ReaderResult.Failure(location = LOCATION, error = JsonErrors.AdditionalItems),
                        ReaderResult.Failure(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                    )
                )
            }
        }
    }
}
