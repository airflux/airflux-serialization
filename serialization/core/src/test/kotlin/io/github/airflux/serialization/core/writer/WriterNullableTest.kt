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

package io.github.airflux.serialization.core.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.test.dummy.DummyWriter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class WriterNullableTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_VALUE = "91a10692-7430-4d58-a465-633d45ea2f4b"

        private val ENV = JsWriterEnv(options = Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The extension-function the nullable" - {

            "when an original reader returns a result as a success" - {
                val writer = DummyWriter.string<Unit>().nullable()

                "when the value is not null" - {
                    val source: String = ID_PROPERTY_VALUE

                    "then should return the written value" {
                        val result = writer.write(ENV, LOCATION, source)

                        result shouldBe JsString(ID_PROPERTY_VALUE)
                    }
                }

                "when the value is null" - {
                    val source: String? = null

                    "then should return the JsNull" {
                        val result = writer.write(ENV, LOCATION, source)

                        result shouldBe JsNull
                    }
                }
            }
        }
    }
}
