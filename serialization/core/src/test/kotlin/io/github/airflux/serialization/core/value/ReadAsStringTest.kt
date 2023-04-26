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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsStringTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty.append("user")
    }

    init {
        "The readAsString function" - {

            "when called with a receiver of the StringNode type" - {

                "should return the string value" {
                    val json: ValueNode = StringNode("abc")
                    val result = json.readAsString(ENV, LOCATION)
                    result shouldBeSuccess ReadingResult.Success(location = LOCATION, value = "abc")
                }
            }

            "when called with a receiver of not the StringNode type" - {

                "should return the invalid type error" {
                    val json: ValueNode = BooleanNode.valueOf(true)
                    val result = json.readAsString(ENV, LOCATION)
                    result shouldBeFailure ReadingResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(StringNode.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
                    )
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
