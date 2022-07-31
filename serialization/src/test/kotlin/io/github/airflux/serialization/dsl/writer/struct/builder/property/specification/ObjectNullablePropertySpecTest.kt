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

package io.github.airflux.serialization.dsl.writer.struct.builder.property.specification

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ObjectNullablePropertySpecTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "8933c39e-e99f-4a3f-b2a1-ea915d690ded"

        private val CONTEXT = WriterContext()
        private val LOCATION = Location.empty
    }

    init {

        "The ObjectPropertySpec#Nullable type" - {

            "when created the instance of a spec of the nullable property" - {
                val from: (String) -> String? = { it }
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = nullable(name = PROPERTY_NAME, from = from, writer = writer)

                "then the property name should equal the passed property name" {
                    spec.name shouldBe PROPERTY_NAME
                }

                "then the value extractor should equal the passed the value extractor" {
                    spec.from shouldBe from
                }

                "then the initialized writer should return a property value" {
                    val result = spec.writer.write(CONTEXT, LOCATION, PROPERTY_VALUE)
                    result shouldBe StringNode(PROPERTY_VALUE)
                }
            }

            "when the filter was added to the spec" - {
                val from: (String) -> String? = { it }
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = nullable(name = PROPERTY_NAME, from = from, writer = writer)
                val specWithFilter = spec.filter { _, _, value -> value.isNotEmpty() }

                "hen the property name should equal the passed property name" {
                    spec.name shouldBe PROPERTY_NAME
                }

                "then the value extractor should equals the passed the value extractor" {
                    spec.from shouldBe from
                }

                "when passing a value that satisfies the predicate for filtering" - {
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, PROPERTY_VALUE)

                    "then a non-null property value should be returned" {
                        result shouldBe StringNode(PROPERTY_VALUE)
                    }
                }

                "when passing a value that does not satisfy the filter predicate" - {
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, "")

                    "then the null value should be returned" {
                        result.shouldBeNull()
                    }
                }
            }
        }
    }
}
