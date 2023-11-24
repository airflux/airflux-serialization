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

package io.github.airflux.serialization.test.dummy

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue

public class DummyReader<EB, O, T>(
    public val result: (JsReaderEnv<EB, O>, context: JsContext, JsLocation, JsValue) -> JsReaderResult<T>
) : JsReader<EB, O, T> {

    public constructor(result: JsReaderResult<T>) : this({ _, _, _, _ -> result })

    override fun read(
        env: JsReaderEnv<EB, O>,
        context: JsContext,
        location: JsLocation,
        source: JsValue
    ): JsReaderResult<T> =
        result(env, context, location, source)

    public companion object {

        @JvmStatic
        public fun <EB, O> boolean(): JsReader<EB, O, Boolean>
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

        @JvmStatic
        public fun <EB, O> string(): JsReader<EB, O, String>
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

        @JvmStatic
        public fun <EB, O> int(): JsReader<EB, O, Int>
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
    }
}
