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
import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsArrayTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT: JsContext = JsContext
        private val LOCATION: JsLocation = JsLocation.append("user")
        private const val USER_NAME = "user"
        private val READER: (JsReaderEnv<EB, Unit>, JsContext, JsLocation, JsArray) -> ReadingResult<List<String>> =
            { _, _, location, source ->
                success(location = location, value = source.map { (it as JsString).get })
            }
    }

    init {
        "The 'readAsArray' function" - {

            "when called with a receiver of the JsArray type" - {

                "should return the collection of values" {
                    val json: JsValue = JsArray(JsString(USER_NAME))

                    val result = json.readAsArray(ENV, CONTEXT, LOCATION, READER)

                    result shouldBeSuccess success(location = LOCATION, value = listOf(USER_NAME))
                }
            }

            "when called with a receiver of not the JsArray type" - {

                "should return the invalid type error" {
                    val json: JsValue = JsBoolean.valueOf(true)

                    val result = json.readAsArray(ENV, CONTEXT, LOCATION, READER)

                    result shouldBeFailure failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(JsArray.nameOfType),
                            actual = JsBoolean.nameOfType
                        )
                    )
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
