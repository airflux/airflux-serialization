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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.assertAsFailure
import io.github.airflux.serialization.common.assertAsSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.kotest.core.spec.style.FreeSpec
import java.math.BigDecimal

internal class ReadAsNumberTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty.append("user")
        private val READER = { _: ReaderEnv<EB, Unit>, location: Location, text: String ->
            ReaderResult.Success(location = location, value = BigDecimal(text))
        }
    }

    init {
        "The readAsNumber function" - {

            "when called with a receiver of the NumericNode#Number type" - {

                "should return the number value" {
                    val json: ValueNode = NumericNode.Number.valueOrNullOf(Int.MAX_VALUE.toString())!!
                    val result = json.readAsNumber(ENV, LOCATION, READER)
                    result.assertAsSuccess(value = BigDecimal(Int.MAX_VALUE))
                }
            }
            "when called with a receiver of not the NumericNode#Number type" - {

                "should return the invalid type error" {
                    val json: ValueNode = BooleanNode.valueOf(true)
                    val result = json.readAsNumber(ENV, LOCATION, READER)
                    result.assertAsFailure(
                        ReaderResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = listOf(NumericNode.Number.nameOfType),
                                actual = BooleanNode.nameOfType
                            )
                        )
                    )
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
