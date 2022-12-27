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

package io.github.airflux.serialization.dsl.writer.struct.property

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class StructNonNullablePropertyTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "205424cf-2ebf-4b65-b3c3-7c848dc8f343"

        private val ENV = WriterEnv(context = Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The StructProperty#NonNullable type" - {

            "when created an instance of the non-nullable property" - {
                val from: (String) -> String = { it }
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = StructPropertySpec.NonNullable(name = PROPERTY_NAME, from = from, writer = writer)
                val property = StructProperty.NonNullable(spec)

                "then the property name should equal the property name from the spec" {
                    property.name shouldBe spec.name
                }
            }

            "when the extractor returns the non-null value" - {
                val from: (String) -> String = { it }

                "when the writer of the property returns the null value" - {
                    val writer: Writer<Unit, String> = DummyWriter { null }
                    val property = createProperty(from = from, writer = writer)

                    "then the method write should return the null value" {
                        val result = property.write(ENV, LOCATION, PROPERTY_VALUE)
                        result.shouldBeNull()
                    }
                }

                "when the writer of the property returns the not null value" - {
                    val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                    val property = createProperty(from = from, writer = writer)

                    "then the method write should return the not null value" {
                        val result = property.write(ENV, LOCATION, PROPERTY_VALUE)
                        result shouldBe StringNode(PROPERTY_VALUE)
                    }
                }
            }
        }
    }

    private fun <CTX, T : Any, P : Any> createProperty(
        from: (T) -> P,
        writer: Writer<CTX, P>
    ): StructProperty.NonNullable<CTX, T, P> =
        StructProperty.NonNullable(
            StructPropertySpec.NonNullable(name = PROPERTY_NAME, from = from, writer = writer)
        )
}
