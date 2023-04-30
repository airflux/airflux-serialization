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

package io.github.airflux.serialization.core.common

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue

internal class DummyReader<EB, O, CTX, T>(
    val result: (ReaderEnv<EB, O>, context: CTX, JsLocation, JsValue) -> ReadingResult<T>
) : JsReader<EB, O, CTX, T> {

    constructor(result: ReadingResult<T>) : this({ _, _, _, _ -> result })

    override fun read(env: ReaderEnv<EB, O>, context: CTX, location: JsLocation, source: JsValue): ReadingResult<T> =
        result(env, context, location, source)

    internal companion object {

        internal fun <EB, O, CTX> boolean(): JsReader<EB, O, CTX, Boolean>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, _, location, source ->
                    if (source is JsBoolean)
                        success(location = location, value = source.get)
                    else
                        failure(
                            location = location,
                            error = env.errorBuilders.invalidTypeError(
                                expected = listOf(JsBoolean.nameOfType),
                                actual = source.nameOfType
                            )
                        )
                }
            )

        internal fun <EB, O, CTX> string(): JsReader<EB, O, CTX, String>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, _, location, source ->
                    if (source is JsString)
                        success(location = location, value = source.get)
                    else
                        failure(
                            location = location,
                            error = env.errorBuilders.invalidTypeError(
                                expected = listOf(JsString.nameOfType),
                                actual = source.nameOfType
                            )
                        )
                }
            )

        internal fun <EB, O, CTX> int(): JsReader<EB, O, CTX, Int>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, _, location, source ->
                    if (source is JsNumeric.Integer)
                        success(location = location, value = source.get.toInt())
                    else
                        failure(
                            location = location,
                            error = env.errorBuilders.invalidTypeError(
                                expected = listOf(JsNumeric.Integer.nameOfType),
                                actual = source.nameOfType
                            )
                        )
                }
            )

        internal fun <EB, O, CTX> long(): JsReader<EB, O, CTX, Long>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, _, location, source ->
                    if (source is JsNumeric)
                        success(location = location, value = source.get.toLong())
                    else
                        failure(
                            location = location,
                            error = env.errorBuilders.invalidTypeError(
                                expected = listOf(JsNumeric.Integer.nameOfType, JsNumeric.Number.nameOfType),
                                actual = source.nameOfType
                            )
                        )
                }
            )
    }
}
