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
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsArrayTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty.append("user")
        private const val USER_NAME = "user"
        private val READER = { _: ReaderEnv<EB, Unit>, _: Unit, location: Location, source: ArrayNode ->
            success(location = location, value = source.map { (it as StringNode).get })
        }
    }

    init {
        "The 'readAsArray' function" - {

            "when called with a receiver of the ArrayNode type" - {

                "should return the collection of values" {
                    val json: ValueNode = ArrayNode(StringNode(USER_NAME))

                    val result = json.readAsArray(ENV, CONTEXT, LOCATION, READER)

                    result shouldBeSuccess success(location = LOCATION, value = listOf(USER_NAME))
                }
            }

            "when called with a receiver of not the ArrayNode type" - {

                "should return the invalid type error" {
                    val json: ValueNode = BooleanNode.valueOf(true)

                    val result = json.readAsArray(ENV, CONTEXT, LOCATION, READER)

                    result shouldBeFailure failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(ArrayNode.nameOfType),
                            actual = BooleanNode.nameOfType
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
