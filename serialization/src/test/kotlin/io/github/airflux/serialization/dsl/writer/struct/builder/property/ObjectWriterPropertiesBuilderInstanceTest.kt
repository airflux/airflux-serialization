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

package io.github.airflux.serialization.dsl.writer.struct.builder.property

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.ObjectPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ObjectWriterPropertiesBuilderInstanceTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "f12720c8-a441-4b18-9783-b8bc7b31607c"

        private val ENV = WriterEnv(context = Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The ObjectWriterPropertiesBuilderInstance type" - {

            "when no property is added to the builder" - {
                val properties = ObjectWriterPropertiesBuilderInstance<Unit, String>().build()

                "should be empty" {
                    properties shouldContainExactly emptyList()
                }
            }

            "when a non-nullable property were added to the builder" - {
                val from: (String) -> String = { it }
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = ObjectPropertySpec.NonNullable(name = PROPERTY_NAME, from = from, writer = writer)
                val properties: ObjectProperties<Unit, String> = ObjectWriterPropertiesBuilderInstance<Unit, String>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the non-nullable property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<ObjectProperty.NonNullable<Unit, *, *>>()
                        it.name shouldBe spec.name
                        it.write(ENV, LOCATION, PROPERTY_VALUE) shouldBe StringNode(PROPERTY_VALUE)
                    }
                }
            }

            "when a optional property were added to the builder" - {
                val from: (String) -> String? = { it }
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = ObjectPropertySpec.Optional(name = PROPERTY_NAME, from = from, writer = writer)
                val properties: ObjectProperties<Unit, String> = ObjectWriterPropertiesBuilderInstance<Unit, String>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the optional property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<ObjectProperty.Optional<Unit, *, *>>()
                        it.name shouldBe spec.name
                        it.write(ENV, LOCATION, PROPERTY_VALUE) shouldBe StringNode(PROPERTY_VALUE)
                    }
                }
            }

            "when a nullable property were added to the builder" - {
                val from: (String) -> String? = { it }
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = ObjectPropertySpec.Nullable(name = PROPERTY_NAME, from = from, writer = writer)
                val properties: ObjectProperties<Unit, String> = ObjectWriterPropertiesBuilderInstance<Unit, String>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the nullable property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<ObjectProperty.Nullable<Unit, *, *>>()
                        it.name shouldBe spec.name
                        it.write(ENV, LOCATION, PROPERTY_VALUE) shouldBe StringNode(PROPERTY_VALUE)
                    }
                }
            }
        }
    }
}
