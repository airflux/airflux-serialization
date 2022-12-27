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

package io.github.airflux.serialization.dsl.writer.array.builder.item

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.dsl.writer.array.item.ArrayItemWriter
import io.github.airflux.serialization.dsl.writer.array.item.specification.ArrayItemSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ArrayOptionalItemWriterTest : FreeSpec() {

    companion object {
        private const val ITEM_VALUE = "value"

        private val ENV = WriterEnv(context = Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The ArrayItems#Optional type" - {

            "when created an instance of the optional item" - {
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val itemWriter: ArrayItemWriter.Optional<Unit, String?> = createItemWriter(writer = writer)

                "when an item is the not null value" - {
                    val value = "value"

                    "then the method write should return the null value" {
                        val result = itemWriter.write(ENV, LOCATION, value)
                        result shouldBe StringNode(ITEM_VALUE)
                    }
                }

                "when an item is the null value" - {
                    val value: String? = null

                    "then the method write should return the not null value" {
                        val result = itemWriter.write(ENV, LOCATION, value)
                        result.shouldBeNull()
                    }
                }
            }
        }
    }

    private fun <CTX, T> createItemWriter(writer: Writer<CTX, T & Any>): ArrayItemWriter.Optional<CTX, T> =
        ArrayItemWriter.Optional(ArrayItemSpec.Optional(writer = writer))
}
