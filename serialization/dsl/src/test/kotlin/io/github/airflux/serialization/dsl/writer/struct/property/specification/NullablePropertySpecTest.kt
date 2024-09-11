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

package io.github.airflux.serialization.dsl.writer.struct.property.specification

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.nullable
import io.github.airflux.serialization.test.dummy.DummyWriter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class NullablePropertySpecTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "89ec69f1-c636-42b8-8e62-6250c4321330"

        private val ENV = JsWriterEnv(config = JsWriterEnv.Config(options = Unit))
        private val LOCATION: JsLocation = JsLocation

        private val WRITER: JsWriter<Unit, String?> = DummyWriter.string<Unit>().nullable()
    }

    init {

        "The builder function for the specification of a nullable property" - {

            "when using an extractor expression without context" - {
                val extractor: (DTO) -> String? = { it.id }
                val spec: JsStructPropertySpec<Unit, DTO, String?> =
                    nullable(name = PROPERTY_NAME, from = extractor, writer = WRITER)

                "then the property name should equal the passed property name" {
                    spec.name shouldBe PROPERTY_NAME
                }

                "then the value extractor should equals the passed the value extractor" {
                    val from = spec.from
                        .shouldBeInstanceOf<JsStructPropertySpec.Extractor.WithoutEnv<Unit, DTO, String>>()
                    from.extractor shouldBe extractor
                }

                "then the initialized writer should return a property value" {
                    val result = spec.writer.write(ENV, LOCATION, PROPERTY_VALUE)
                    result shouldBe JsString(PROPERTY_VALUE)
                }
            }

            "when using an extractor expression with context" - {
                val extractor: (DTO, JsWriterEnv<Unit>) -> String? = { value, _ -> value.id }
                val spec: JsStructPropertySpec<Unit, DTO, String?> =
                    nullable(name = PROPERTY_NAME, from = extractor, writer = WRITER)

                "then the property name should equal the passed property name" {
                    spec.name shouldBe PROPERTY_NAME
                }

                "then the value extractor should equals the passed the value extractor" {
                    val from =
                        spec.from.shouldBeInstanceOf<JsStructPropertySpec.Extractor.WithEnv<Unit, DTO, String>>()
                    from.extractor shouldBe extractor
                }

                "then the initialized writer should return a property value" {
                    val result = spec.writer.write(ENV, LOCATION, PROPERTY_VALUE)
                    result shouldBe JsString(PROPERTY_VALUE)
                }
            }

            "when some filter was added to the spec" - {
                val extractor: (DTO) -> String? = { it.id }
                val spec: JsStructPropertySpec<Unit, DTO, String?> =
                    nullable(name = PROPERTY_NAME, from = extractor, writer = WRITER)
                val specWithFilter = spec.filter { _, _, value -> value.isNotEmpty() }

                "when passing a value that satisfies the predicate for filtering" - {
                    val result = specWithFilter.writer.write(ENV, LOCATION, PROPERTY_VALUE)

                    "then a non-null property value should be returned" {
                        result shouldBe JsString(PROPERTY_VALUE)
                    }
                }

                "when passing a value that does not satisfy the filter predicate" - {
                    val result = specWithFilter.writer.write(ENV, LOCATION, "")

                    "then the null value should be returned" {
                        result.shouldBeNull()
                    }
                }
            }
        }
    }

    internal class DTO(val id: String?)
}
