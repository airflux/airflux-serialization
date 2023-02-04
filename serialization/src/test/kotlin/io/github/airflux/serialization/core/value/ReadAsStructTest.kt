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

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.kotest.shouldBeFailure
import io.github.airflux.serialization.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsStructTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty.append("user")
        private const val USER_NAME = "user"
        private val reader = { _: ReaderEnv<EB, Unit>, _: Unit, location: Location, source: StructNode ->
            val name = source["name"] as StringNode
            ReaderResult.Success(location = location, value = DTO(name = name.get))
        }
    }

    init {
        "The 'readAsStruct' function" - {

            "when called with a receiver of the StructNode type" - {

                "should return the DTO" {
                    val json: ValueNode = StructNode("name" to StringNode(USER_NAME))
                    val result = json.readAsStruct(ENV, CONTEXT, LOCATION, reader)
                    result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = DTO(name = USER_NAME))
                }
            }
            "when called with a receiver of not the StructNode type" - {

                "should return the invalid type' error" {
                    val json: ValueNode = BooleanNode.valueOf(true)
                    val result = json.readAsStruct(ENV, CONTEXT, LOCATION, reader)
                    result shouldBeFailure ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(StructNode.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
                    )
                }
            }
        }
    }

    private data class DTO(val name: String)

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
