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
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsStringTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext(
            InvalidTypeErrorBuilder(JsonErrors::InvalidType)
        )
        private val LOCATION = Location.empty.append("user")
    }

    init {
        "The readAsString function" - {

            "when called with a receiver of the StringNode type" - {

                "should return the string value" {
                    val json: ValueNode = StringNode("abc")
                    val result = json.readAsString(CONTEXT, LOCATION)
                    result.assertAsSuccess(value = "abc")
                }
            }

            "when called with a receiver of not the StringNode type" - {

                "should return the invalid type error" {
                    val json: ValueNode = BooleanNode.valueOf(true)
                    val result = json.readAsString(CONTEXT, LOCATION)
                    result.assertAsFailure(
                        ReaderResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = ValueNode.Type.STRING,
                                actual = ValueNode.Type.BOOLEAN
                            )
                        )
                    )
                }
            }
        }
    }
}
