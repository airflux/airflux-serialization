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

package io.github.airflux.dsl.writer.`object`.builder.property.specification

import io.github.airflux.common.DummyWriter
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsObjectNullablePropertySpecTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "id"
        private const val ATTRIBUTE_VALUE = "8933c39e-e99f-4a3f-b2a1-ea915d690ded"

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsObjectPropertySpec#Nullable" - {

            "when created the instance of a spec of the nullable property" - {
                val spec = nullable(name = "id", from = DTO::value, writer = DummyWriter { JsString(it) })

                "then the attribute name parameter should equals the passed attribute name" {
                    spec.name shouldBe ATTRIBUTE_NAME
                }

                "when a writer was initialized" - {

                    "then the writer should return a non-null attribute value if the value being written is non-null" {
                        val input = DTO(value = ATTRIBUTE_VALUE)
                        val result = spec.writer.write(CONTEXT, LOCATION, spec.from(input))
                        result shouldBe JsString(ATTRIBUTE_VALUE)
                    }

                    "then the writer should return a null attribute value if the value being written is the null" {
                        val input = DTO(value = null)
                        val result = spec.writer.write(CONTEXT, LOCATION, spec.from(input))
                        result shouldBe JsNull
                    }
                }
            }

            "when the filter was added to the spec" - {
                val spec = nullable(name = "id", from = DTO::value, writer = DummyWriter { JsString(it) })
                val specWithFilter = spec.filter { _, _, value -> value.isNotEmpty() }

                "if the value was not filtered" - {
                    val input = DTO(value = ATTRIBUTE_VALUE)
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, spec.from(input))

                    "then a non-null attribute value should be returned" {
                        result.shouldNotBeNull()
                        result shouldBe JsString(ATTRIBUTE_VALUE)
                    }
                }

                "if the value was filtered" - {
                    val input = DTO(value = "")
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, spec.from(input))

                    "then the null value should be returned" {
                        result.shouldBeNull()
                    }
                }
            }
        }
    }

    internal data class DTO(val value: String?)
}
