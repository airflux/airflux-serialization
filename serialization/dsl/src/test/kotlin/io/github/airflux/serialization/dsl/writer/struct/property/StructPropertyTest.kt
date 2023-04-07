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

package io.github.airflux.serialization.dsl.writer.struct.property

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.dsl.common.DummyWriter
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructPropertyTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "205424cf-2ebf-4b65-b3c3-7c848dc8f343"

        private val ENV = WriterEnv(options = Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty

        private val WRITER: Writer<Unit, Unit, String> = DummyWriter.stringWriter()
    }

    init {

        "The StructProperty type" - {

            "when using an expression the from without context in spec" - {
                val spec = StructPropertySpec<Unit, Unit, DTO, String>(
                    name = PROPERTY_NAME,
                    from = StructPropertySpec.Extractor.WithoutContext { id },
                    writer = WRITER
                )

                "when created an instance of an property" - {
                    val property = StructProperty(spec)

                    "then the property name should equal the property name from the spec" {
                        property.name shouldBe spec.name
                    }

                    "then the method write should return the value" {
                        val result = property.write(ENV, CONTEXT, LOCATION, DTO(id = PROPERTY_VALUE))
                        result shouldBe StringNode(PROPERTY_VALUE)
                    }
                }
            }

            "when using an expression the from with context in spec" - {
                val spec = StructPropertySpec<Unit, Unit, DTO, String>(
                    name = PROPERTY_NAME,
                    from = StructPropertySpec.Extractor.WithContext { id },
                    writer = WRITER
                )

                "when created an instance of an property" - {
                    val property = StructProperty(spec)

                    "then the property name should equal the property name from the spec" {
                        property.name shouldBe spec.name
                    }

                    "then the method write should return the value" {
                        val result = property.write(ENV, CONTEXT, LOCATION, DTO(id = PROPERTY_VALUE))
                        result shouldBe StringNode(PROPERTY_VALUE)
                    }
                }
            }
        }
    }

    internal class DTO(val id: String)
}
