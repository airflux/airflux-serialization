/*
 * Copyright 2021-2024 Maxim Sambulat.
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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.test.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsReaderResultFailureTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "A JsReaderResult#Failure type" - {

            "constructor(JsLocation, JsError)" {
                val failure = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                failure.causes.toList() shouldContainExactly listOf(
                    JsReaderResult.Failure.Cause(
                        location = LOCATION,
                        error = JsonErrors.PathMissing
                    )
                )
            }

            "constructor(JsLocation, JsReaderResult#Errors)" {
                val failure = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                failure.causes.toList() shouldContainExactly listOf(
                    JsReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)
                )
            }

            "the function JsReaderResult#Failure#plus " {
                val firstFailure = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                val secondFailure = JsReaderResult.Failure(
                    location = LOCATION,
                    error = JsonErrors.InvalidType(
                        expected = listOf(JsString.nameOfType),
                        actual = JsBoolean.nameOfType
                    )
                )

                val failure = firstFailure + secondFailure

                failure.causes.toList() shouldContainExactly listOf(
                    JsReaderResult.Failure.Cause(
                        location = LOCATION,
                        error = JsonErrors.PathMissing
                    ),
                    JsReaderResult.Failure.Cause(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(JsString.nameOfType),
                            actual = JsBoolean.nameOfType
                        )
                    )
                )
            }

            "should comply with equals() and hashCode() contract" {
                JsReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing).shouldBeEqualsContract(
                    y = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    z = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    others = listOf(
                        JsReaderResult.Failure(location = LOCATION, error = JsonErrors.AdditionalItems),
                        JsReaderResult.Failure(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                    )
                )
            }
        }
    }
}
