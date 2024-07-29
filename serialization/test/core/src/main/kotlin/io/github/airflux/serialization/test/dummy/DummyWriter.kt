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
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv

public class DummyWriter<O, T>(public val result: (T) -> JsValue?) : JsWriter<O, T> {
    override fun write(env: JsWriterEnv<O>, location: JsLocation, source: T): JsValue? =
        result(source)

    public companion object {

        @JvmStatic
        public fun <O> int(): JsWriter<O, Int> =
            DummyWriter(result = { source -> JsNumber.valueOf(source.toString())!! })

        @JvmStatic
        public fun <O> string(): JsWriter<O, String> =
            DummyWriter(result = { source -> JsString(source) })
    }
}
