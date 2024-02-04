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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class BooleanWriterTest : FreeSpec() {

    companion object {
        private val ENV = JsWriterEnv(options = Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The boolean type writer" - {
            val writer: JsWriter<Unit, Boolean> = booleanWriter()

            "when the value contains the true value" - {
                val value = true

                "then writer should return the JsBoolean.True value" {
                    val result = writer.write(ENV, LOCATION, value)
                    result shouldBe JsBoolean.True
                }
            }

            "when the value contains the false value" - {
                val value = false

                "then writer should return the JsBoolean.False value" {
                    val result = writer.write(ENV, LOCATION, value)
                    result shouldBe JsBoolean.False
                }
            }
        }
    }
}
