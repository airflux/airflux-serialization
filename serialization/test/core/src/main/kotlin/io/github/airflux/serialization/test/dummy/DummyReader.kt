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

package io.github.airflux.serialization.test.dummy

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue

public class DummyReader<EB, O, T>(
    public val result: (JsReaderEnv<EB, O>, JsLocation, JsValue) -> JsReaderResult<T>
) : JsReader<EB, O, T> {

    public constructor(result: JsReaderResult<T>) : this({ _, _, _ -> result })

    override fun read(
        env: JsReaderEnv<EB, O>,
        location: JsLocation,
        source: JsValue
    ): JsReaderResult<T> =
        result(env, location, source)

    public companion object {

        @JvmStatic
        public fun <EB, O> boolean(): JsReader<EB, O, Boolean>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, location, source ->
                    if (source is JsBoolean)
                        success(location = location, value = source.get)
                    else
                        failure(
                            location = location,
                            error = env.config.errorBuilders.invalidTypeError(
                                expected = JsValue.Type.BOOLEAN,
                                actual = source.type
                            )
                        )
                }
            )

        @JvmStatic
        public fun <EB, O> string(): JsReader<EB, O, String>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, location, source ->
                    if (source is JsString)
                        success(location = location, value = source.get)
                    else
                        failure(
                            location = location,
                            error = env.config.errorBuilders.invalidTypeError(
                                expected = JsValue.Type.STRING,
                                actual = source.type
                            )
                        )
                }
            )

        @JvmStatic
        public fun <EB, O> int(): JsReader<EB, O, Int>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, location, source ->
                    if (source is JsNumber)
                        success(location = location, value = source.get.toInt())
                    else
                        failure(
                            location = location,
                            error = env.config.errorBuilders.invalidTypeError(
                                expected = JsValue.Type.NUMBER,
                                actual = source.type
                            )
                        )
                }
            )
    }
}
