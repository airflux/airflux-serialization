/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.serialization.core.writer.`object`

import io.github.airflux.serialization.common.DummyObjectWriter
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class OptionalFieldWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The writeNullable function" - {
            val writer: JsWriter<String> = DummyObjectWriter { JsString(it) }

            "when a value is not null" - {
                val value = "value"

                "should return the JsString value" {
                    val result: JsValue? =
                        writeOptional(context = CONTEXT, location = LOCATION, using = writer, value = value)
                    result shouldBe JsString(value)
                }
            }

            "when a value is null" - {
                val value: String? = null

                "should return the null value" {
                    val result: JsValue? =
                        writeOptional(context = CONTEXT, location = LOCATION, using = writer, value = value)
                    result.shouldBeNull()
                }
            }
        }
    }
}
