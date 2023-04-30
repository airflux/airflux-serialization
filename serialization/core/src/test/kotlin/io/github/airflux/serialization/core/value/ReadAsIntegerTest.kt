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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsIntegerTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation.append("user")
        private val READER = { _: ReaderEnv<EB, Unit>, _: Unit, location: JsLocation, text: String ->
            success(location = location, value = text.toInt())
        }
    }

    init {
        "The readAsInteger function" - {

            "when called with a receiver of the JsNumeric#Integer type" - {

                "should return the number value" {
                    val json: JsValue = JsNumeric.valueOf(Int.MAX_VALUE)
                    val result = json.readAsInteger(ENV, CONTEXT, LOCATION, READER)
                    result shouldBeSuccess success(location = LOCATION, value = Int.MAX_VALUE)
                }
            }
            "when called with a receiver of not the JsNumeric#Integer type" - {

                "should return the invalid type error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsInteger(ENV, CONTEXT, LOCATION, READER)
                    result shouldBeFailure failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(JsNumeric.Integer.nameOfType),
                            actual = JsBoolean.nameOfType
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
