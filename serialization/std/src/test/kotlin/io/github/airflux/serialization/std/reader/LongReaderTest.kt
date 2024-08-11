/*
 * Copyright 2021-2024 Maxim Sambulat.
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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.NumberFormatErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import java.math.BigInteger
import kotlin.reflect.KClass

internal class LongReaderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private val LongReader = LongReader<EB, Unit>()
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
                    val source: JsValue = JsNumber.valueOf(value.toString())!!
                    val result = LongReader.read(ENV, LOCATION, source)
                    result.shouldBeSuccess(location = LOCATION, value = value)
                }
            }

            "should return the invalid type error" {
                val source: JsValue = JsString("abc")
                val result = LongReader.read(ENV, LOCATION, source)
                result.shouldBeFailure(
                    location = JsLocation,
                    error = JsonErrors.InvalidType(
                        expected = JsValue.Type.NUMBER,
                        actual = JsValue.Type.STRING
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
                            Long.MIN_VALUE.toString() + "0"
                        ),
                        Pair(
                            "The value is in an invalid format, positive with a fractional part.",
                            Long.MAX_VALUE.toString() + "0"
                        ),
                    )
                ) { (_, value) ->
                    val source = JsNumber.valueOf(value)!!
                    val result = LongReader.read(ENV, LOCATION, source)
                    result.shouldBeFailure(
                        location = JsLocation,
                        error = JsonErrors.ValueCast(value = value, type = Long::class)
                    )
                }
            }
        }
    }

    private fun getLessValue(): String = (Long.MIN_VALUE.toBigInteger() - BigInteger.ONE).toString()
    private fun getMoreValue(): String = (Long.MAX_VALUE.toBigInteger() + BigInteger.ONE).toString()

    internal class EB : InvalidTypeErrorBuilder,
                        NumberFormatErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun numberFormatError(value: String, target: KClass<*>): JsReaderResult.Error =
            JsonErrors.ValueCast(value, target)
    }
}
