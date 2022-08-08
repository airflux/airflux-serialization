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
import io.github.airflux.serialization.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData

internal class ByteReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext(
            listOf(
                InvalidTypeErrorBuilder(JsonErrors::InvalidType),
                ValueCastErrorBuilder(JsonErrors::ValueCast)
            )
        )
    }

    init {

        "The byte type reader" - {

            "should return the byte value" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is an equal minimum of the allowed range", Byte.MIN_VALUE),
                        Pair("Value is an equal maximum of the allowed range", Byte.MAX_VALUE)
                    )
                ) { (_, value) ->
                    val source: ValueNode = NumberNode.valueOf(value)
                    val result = ByteReader.read(CONTEXT, Location.empty, source)
                    result.assertAsSuccess(location = Location.empty, value = value)
                }
            }

            "should return the invalid type error" {
                val source: ValueNode = StringNode("abc")
                val result = ByteReader.read(CONTEXT, Location.empty, source)
                result.assertAsFailure(
                    ReaderResult.Failure.Cause(
                        location = Location.empty,
                        error = JsonErrors.InvalidType(expected = ValueNode.Type.NUMBER, actual = ValueNode.Type.STRING)
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
                    val source = NumberNode.valueOf(value)!!
                    val result = ByteReader.read(CONTEXT, Location.empty, source)
                    result.assertAsFailure(
                        ReaderResult.Failure.Cause(
                            location = Location.empty,
                            error = JsonErrors.ValueCast(value = value, type = Byte::class)
                        )
                    )
                }
            }
        }
    }

    private fun getLessValue(): String = (Byte.MIN_VALUE.toInt() - 1).toString()
    private fun getMoreValue(): String = (Byte.MAX_VALUE.toInt() + 1).toString()
}
