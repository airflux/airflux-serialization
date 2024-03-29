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
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import kotlin.reflect.KClass

internal class ByteReaderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private val ByteReader = byteReader<EB, Unit>()
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
                    val source: JsValue = JsNumeric.valueOf(value)
                    val result = ByteReader.read(ENV, LOCATION, source)
                    result shouldBeSuccess success(location = LOCATION, value = value)
                }
            }

            "should return the invalid type error" {
                val source: JsValue = JsString("abc")
                val result = ByteReader.read(ENV, LOCATION, source)
                result shouldBeFailure failure(
                    location = JsLocation,
                    error = JsonErrors.InvalidType(
                        expected = listOf(JsNumeric.Integer.nameOfType),
                        actual = JsString.nameOfType
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
                            Short.MIN_VALUE.toString()
                        ),
                        Pair(
                            "The value is in an invalid format, positive with a fractional part.",
                            Short.MAX_VALUE.toString()
                        ),
                    )
                ) { (_, value) ->
                    val source = JsNumeric.Integer.valueOrNullOf(value)!!
                    val result = ByteReader.read(ENV, LOCATION, source)
                    result shouldBeFailure failure(
                        location = JsLocation,
                        error = JsonErrors.ValueCast(value = value, type = Byte::class)
                    )
                }
            }
        }
    }

    private fun getLessValue(): String = (Byte.MIN_VALUE.toInt() - 1).toString()
    private fun getMoreValue(): String = (Byte.MAX_VALUE.toInt() + 1).toString()

    internal class EB : InvalidTypeErrorBuilder,
                        NumberFormatErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun numberFormatError(value: String, target: KClass<*>): JsReaderResult.Error =
            JsonErrors.ValueCast(value, target)
    }
}
