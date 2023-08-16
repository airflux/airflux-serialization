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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class IntWriterTest : FreeSpec() {

    companion object {
        private val ENV = JsWriterEnv(options = Unit)
        private val CONTEXT: JsContext = JsContext
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The int type writer" - {
            val writer: JsWriter<Unit, Int> = intWriter()
            val value: Int = Int.MAX_VALUE

            "should return the JsNumeric value" {
                val result = writer.write(ENV, CONTEXT, LOCATION, value)
                result shouldBe JsNumeric.valueOf(value)
            }
        }
    }
}
