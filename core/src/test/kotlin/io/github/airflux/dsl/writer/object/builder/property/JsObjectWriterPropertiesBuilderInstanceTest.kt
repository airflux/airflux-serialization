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
import io.github.airflux.core.value.JsString
import io.github.airflux.dsl.writer.`object`.builder.property.specification.JsObjectPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class JsObjectWriterPropertiesBuilderInstanceTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "attribute-id"
    }

    init {

        "The JsObjectWriterPropertiesBuilderInstance type" - {

            "when no property is added to the builder" - {
                val properties = JsObjectWriterPropertiesBuilderInstance<DTO>().build()

                "should be empty" {
                    properties shouldContainExactly emptyList()
                }
            }

            "when a required property were added to the builder" - {
                val spec = JsObjectPropertySpec.Required(
                    name = ATTRIBUTE_NAME,
                    from = DTO::value,
                    writer = DummyWriter { JsString(it) }
                )
                val properties: JsObjectProperties<DTO> = JsObjectWriterPropertiesBuilderInstance<DTO>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the required property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Required<*, *>>()
                        it.name shouldBe spec.name
                        it.writer shouldBe spec.writer
                    }
                }
            }

            "when a optional property were added to the builder" - {
                val spec = JsObjectPropertySpec.Optional(
                    name = ATTRIBUTE_NAME,
                    from = DTO::value,
                    writer = DummyWriter { JsString(it!!) }
                )
                val properties: JsObjectProperties<DTO> = JsObjectWriterPropertiesBuilderInstance<DTO>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the optional property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Optional<*, *>>()
                        it.name shouldBe spec.name
                        it.writer shouldBe spec.writer
                    }
                }
            }

            "when a nullable property were added to the builder" - {
                val spec = JsObjectPropertySpec.Nullable(
                    name = ATTRIBUTE_NAME,
                    from = DTO::value,
                    writer = DummyWriter { JsString(it!!) }
                )
                val properties: JsObjectProperties<DTO> = JsObjectWriterPropertiesBuilderInstance<DTO>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the nullable property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Nullable<*, *>>()
                        it.name shouldBe spec.name
                        it.writer shouldBe spec.writer
                    }
                }
            }
        }
    }

    internal data class DTO(val value: String)
}
