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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.std.common.kotest.shouldBeFailure
import io.github.airflux.serialization.std.common.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import kotlin.reflect.KClass

internal class IntReaderTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location
        private val IntReader = intReader<EB, Unit, Unit>()
    }

    init {

        "The int type reader" - {

            "should return the int value" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is an equal minimum of the allowed range", Int.MIN_VALUE),
                        Pair("Value is an equal maximum of the allowed range", Int.MAX_VALUE)
                    )
                ) { (_, value) ->
                    val source: ValueNode = NumericNode.valueOf(value)
                    val result = IntReader.read(ENV, CONTEXT, LOCATION, source)
                    result shouldBeSuccess success(location = LOCATION, value = value)
                }
            }

            "should return the invalid type error" {
                val source: ValueNode = StringNode("abc")
                val result = IntReader.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeFailure failure(
                    location = Location,
                    error = JsonErrors.InvalidType(
                        expected = listOf(NumericNode.Integer.nameOfType),
                        actual = StringNode.nameOfType
                    )
                )
            }

            "should return the value cast error" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is less than the allowed range", getLessValue()),
                        Pair("Value is more than the allowed range", getMoreValue()),
                        Pair(
                            "The value is in an invalid format, negative with a fractional part",
                            Long.MIN_VALUE.toString()
                        ),
                        Pair(
                            "The value is in an invalid format, positive with a fractional part.",
                            Long.MAX_VALUE.toString()
                        ),
                    )
                ) { (_, value) ->
                    val source = NumericNode.Integer.valueOrNullOf(value)!!
                    val result = IntReader.read(ENV, CONTEXT, LOCATION, source)
                    result shouldBeFailure failure(
                        location = Location,
                        error = JsonErrors.ValueCast(value = value, type = Int::class)
                    )
                }
            }
        }
    }

    private fun getLessValue(): String = (Int.MIN_VALUE.toLong() - 1).toString()
    private fun getMoreValue(): String = (Int.MAX_VALUE.toLong() + 1).toString()

    internal class EB : InvalidTypeErrorBuilder,
        ValueCastErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun valueCastError(value: String, target: KClass<*>): ReadingResult.Error =
            JsonErrors.ValueCast(value, target)
    }
}
