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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.assertAsFailure
import io.github.airflux.serialization.common.assertAsSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import java.math.BigDecimal

internal class BigDecimalReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
    }

    init {

        "The big decimal type reader" - {

            "should return the big decimal value" - {
                withData(
                    listOf("-10.5", "-10", "-0.5", "0", "0.5", "10", "10.5")
                ) { value ->
                    val input: ValueNode = NumberNode.valueOf(value)!!
                    val result = BigDecimalReader.read(CONTEXT, Location.empty, input)
                    result.assertAsSuccess(location = Location.empty, value = BigDecimal(value))
                }
            }

            "should return the invalid type error" {
                val input: ValueNode = StringNode("abc")
                val result = BigDecimalReader.read(CONTEXT, Location.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = Location.empty,
                        error = JsonErrors.InvalidType(expected = ValueNode.Type.NUMBER, actual = ValueNode.Type.STRING)
                    )
                )
            }
        }
    }
}
