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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import java.math.BigInteger

internal class LongReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(
            listOf(
                InvalidTypeErrorBuilder(JsonErrors::InvalidType),
                ValueCastErrorBuilder(JsonErrors::ValueCast)
            )
        )
    }

    init {

        "The long type reader" - {

            "should return the long value" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is an equal minimum of the allowed range", Long.MIN_VALUE),
                        Pair("Value is an equal maximum of the allowed range", Long.MAX_VALUE)
                    )
                ) { (_, value) ->
                    val input: JsValue = JsNumber.valueOf(value)
                    val result = LongReader.read(CONTEXT, JsLocation.empty, input)
                    result.assertAsSuccess(location = JsLocation.empty, value = value)
                }
            }

            "should return the invalid type error" {
                val input: JsValue = JsString("abc")
                val result = LongReader.read(CONTEXT, JsLocation.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.STRING)
                    )
                )
            }

            "should return the value cast error" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is less than the allowed range", getLessValue()),
                        Pair("Value is more than the allowed range", getMoreValue()),
                        Pair("The value is in an invalid format, negative with a fractional part", "-10.5"),
                        Pair("The value is in an invalid format, positive with a fractional part.", "10.5"),
                    )
                ) { (_, value) ->
                    val input = JsNumber.valueOf(value)!!
                    val result = LongReader.read(CONTEXT, JsLocation.empty, input)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = JsLocation.empty,
                            error = JsonErrors.ValueCast(value = value, type = Long::class)
                        )
                    )
                }
            }
        }
    }

    private fun getLessValue(): String = (Long.MIN_VALUE.toBigInteger() - BigInteger.ONE).toString()
    private fun getMoreValue(): String = (Long.MAX_VALUE.toBigInteger() + BigInteger.ONE).toString()
}
