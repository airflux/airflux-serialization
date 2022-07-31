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

package io.github.airflux.serialization.dsl.writer.`object`.builder

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ObjectWriterBuilderTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "name"
        private const val PROPERTY_VALUE = "user"

        private val CONTEXT = WriterContext()
        private val LOCATION = Location.empty
    }

    init {

        "The ObjectWriterBuilder type" - {

            "when have some propertys for writing to an object" - {
                val from: (String) -> String = { it }
                val writer = writer {
                    property(nonNullable(name = PROPERTY_NAME, from = from, writer = DummyWriter { StringNode(it) }))
                }

                "then should return the object with some propertys" {
                    val result = writer.write(context = CONTEXT, location = LOCATION, value = PROPERTY_VALUE)
                    result shouldBe StructNode(PROPERTY_NAME to StringNode(PROPERTY_VALUE))
                }
            }

            "when no propertys for writing to an object" - {
                val from: (String) -> String? = { null }

                "when the action of the writer was not set" - {
                    val writer = writer {
                        property(optional(name = PROPERTY_NAME, from = from, DummyWriter { StringNode(it) }))
                    }

                    "then should return the empty value of the StructNode type" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = PROPERTY_VALUE)
                        result shouldBe StructNode()
                    }
                }

                "when the action of the writer was set to return empty value" - {
                    val writer = writer {
                        actionIfEmpty = returnEmptyValue()
                        property(optional(name = PROPERTY_NAME, from = from, DummyWriter { StringNode(it) }))
                    }

                    "then should return the empty value of the StructNode type" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = PROPERTY_VALUE)
                        result shouldBe StructNode()
                    }
                }

                "when the action of the writer was set to return nothing" - {
                    val writer = writer {
                        actionIfEmpty = returnNothing()
                        property(optional(name = PROPERTY_NAME, from = from, DummyWriter { StringNode(it) }))
                    }

                    "then should return the null value" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = PROPERTY_VALUE)
                        result.shouldBeNull()
                    }
                }

                "when the action of the writer was set to return null value" - {
                    val writer = writer {
                        actionIfEmpty = returnNullValue()
                        property(optional(name = PROPERTY_NAME, from = from, DummyWriter { StringNode(it) }))
                    }

                    "then should return the NullNode value" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = PROPERTY_VALUE)
                        result shouldBe NullNode
                    }
                }
            }
        }
    }
}
