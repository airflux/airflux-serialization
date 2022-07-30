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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.ArrayItemSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ArrayNullableItemsTest : FreeSpec() {

    companion object {
        private const val ITEM_VALUE = "value"

        private val CONTEXT = WriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The ArrayItems#Nullable type" - {

            "when created an instance of the nullable item" - {
                val writer = DummyWriter<String> { StringNode(it) }
                val items: ArrayItems.Nullable<String?> = createItems(writer = writer)

                "when an item is the not null value" - {
                    val value = "value"

                    "then the method write should return the null value" {
                        val result = items.write(CONTEXT, LOCATION, value)
                        result shouldBe StringNode(ITEM_VALUE)
                    }
                }

                "when an item is the null value" - {
                    val value: String? = null

                    "then the method write should return the NullNode value" {
                        val result = items.write(CONTEXT, LOCATION, value)
                        result shouldBe NullNode
                    }
                }
            }
        }
    }

    private fun <T> createItems(writer: DummyWriter<T & Any>): ArrayItems.Nullable<T> =
        ArrayItems.Nullable(ArrayItemSpec.Nullable(writer = writer))
}
