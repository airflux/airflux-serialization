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

package io.github.airflux.dsl.writer.array.builder.item.specification

import io.github.airflux.common.DummyWriter
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayOptionalItemSpecTest : FreeSpec() {

    companion object {
        private const val VALUE = "value"

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsArrayItemSpec#Optional" - {

            "when created the instance of a spec of the optional item" - {
                val spec = optional(writer = DummyWriter<String> { JsString(it) })

                "when a writer was initialized" - {

                    "then the writer should return a non-null item value if the value being written is non-null" {
                        val result = spec.writer.write(CONTEXT, LOCATION, VALUE)
                        result shouldBe JsString(VALUE)
                    }

                    "then the writer should return the null value if the value being written is the null" {
                        val result = spec.writer.write(CONTEXT, LOCATION, null)
                        result.shouldBeNull()
                    }
                }
            }

            "when the filter was added to the spec" - {
                val spec = optional(writer = DummyWriter<String> { JsString(it) })
                val specWithFilter = spec.filter { _, _, value -> value.isNotEmpty() }

                "if the value was not filtered" - {
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, VALUE)

                    "then a non-null item value value should be returned" {
                        result shouldBe JsString(VALUE)
                    }
                }

                "if the value was filtered" - {
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, "")

                    "then the null value should be returned" {
                        result.shouldBeNull()
                    }
                }
            }
        }
    }
}
