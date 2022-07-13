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

package io.github.airflux.dsl.writer.`object`.builder.property

import io.github.airflux.common.DummyWriter
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.dsl.writer.`object`.builder.property.specification.nullable
import io.github.airflux.dsl.writer.`object`.builder.property.specification.optional
import io.github.airflux.dsl.writer.`object`.builder.property.specification.required
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsObjectPropertyTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "id"
        private const val ATTRIBUTE_VALUE = "205424cf-2ebf-4b65-b3c3-7c848dc8f343"

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsObjectProperty type" - {

            "when created an instance of the required property" - {
                val spec = required(name = ATTRIBUTE_NAME, from = DTO::value, writer = DummyWriter { JsString(it) })
                val property = JsObjectProperty.Required(spec)

                "then the attribute name should equal the attribute name from the spec" {
                    property.name shouldBe spec.name
                }

                "then the extractor should equal the extractor from the spec" {
                    property.from shouldBe spec.from
                }

                "then the writer should equal the writer from the spec" {
                    property.writer shouldBe spec.writer
                }

                "then the method write should return an attribute value" {
                    property.write(CONTEXT, LOCATION, DTO(value = ATTRIBUTE_VALUE)) shouldBe JsString(ATTRIBUTE_VALUE)
                }
            }

            "when created an instance of the optional property" - {
                val spec = optional(name = ATTRIBUTE_NAME, from = DTO::value, writer = DummyWriter { JsString(it) })
                val property = JsObjectProperty.Optional(spec)

                "then the attribute name should equal the attribute name from the spec" {
                    property.name shouldBe spec.name
                }

                "then the extractor should equal the extractor from the spec" {
                    property.from shouldBe spec.from
                }

                "then the writer should equal the writer from the spec" {
                    property.writer shouldBe spec.writer
                }

                "then the method write should return an attribute value" {
                    property.write(CONTEXT, LOCATION, DTO(value = ATTRIBUTE_VALUE)) shouldBe JsString(ATTRIBUTE_VALUE)
                }
            }

            "when created an instance of the nullable property" - {
                val spec = nullable(name = ATTRIBUTE_NAME, from = DTO::value, writer = DummyWriter { JsString(it) })
                val property = JsObjectProperty.Nullable(spec)

                "then the attribute name should equal the attribute name from the spec" {
                    property.name shouldBe spec.name
                }

                "then the extractor should equal the extractor from the spec" {
                    property.from shouldBe spec.from
                }

                "then the writer should equal the writer from the spec" {
                    property.writer shouldBe spec.writer
                }

                "then the method write should return an attribute value" {
                    property.write(CONTEXT, LOCATION, DTO(value = ATTRIBUTE_VALUE)) shouldBe JsString(ATTRIBUTE_VALUE)
                }
            }
        }
    }

    internal data class DTO(val value: String)
}
