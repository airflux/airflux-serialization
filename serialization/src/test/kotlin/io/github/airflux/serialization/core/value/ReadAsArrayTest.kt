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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReadAsArrayTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private const val USER_NAME = "user"
        private val LOCATION = JsLocation.empty.append("user")
        private val READER = { _: JsReaderContext, location: JsLocation, input: ArrayNode<*> ->
            val result = input.map { (it as StringNode).get }
            JsResult.Success(location, result)
        }
    }

    init {
        "The 'readAsArray' function" - {

            "when called with a receiver of the ArrayNode type" - {

                "should return the collection of values" {
                    val json: ValueNode = ArrayNode(StringNode(USER_NAME))

                    val result = json.readAsArray(CONTEXT, LOCATION, READER)

                    result as JsResult.Success
                    result shouldBe JsResult.Success(location = LOCATION, value = listOf(USER_NAME))
                }
            }

            "when called with a receiver of not the ArrayNode type" - {

                "should return the invalid type error" {
                    val json: ValueNode = BooleanNode.valueOf(true)

                    val result = json.readAsArray(CONTEXT, LOCATION, READER)

                    result as JsResult.Failure
                    result shouldBe JsResult.Failure(
                        location = LOCATION, error = JsonErrors.InvalidType(
                            expected = ValueNode.Type.ARRAY,
                            actual = ValueNode.Type.BOOLEAN
                        )
                    )
                }
            }
        }
    }
}
