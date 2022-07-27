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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsObjectTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private const val USER_NAME = "user"
        private val LOCATION = JsLocation.empty.append("user")
        private val reader = { _: JsReaderContext, location: JsLocation, input: JsObject ->
            val name = input["name"] as JsString
            JsResult.Success(location, DTO(name = name.get))
        }
    }

    init {
        "The 'readAsObject' function" - {
            "when called with a receiver of a 'JsObject'" - {
                "should return the DTO" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val result = json.readAsObject(CONTEXT, LOCATION, reader)
                    result.assertAsSuccess(location = LOCATION, value = DTO(name = USER_NAME))
                }
            }
            "when called with a receiver of a not 'JsObject'" - {
                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsObject(CONTEXT, LOCATION, reader)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.OBJECT,
                                actual = JsValue.Type.BOOLEAN
                            )
                        )
                    )
                }
            }
        }
    }

    private data class DTO(val name: String)
}
