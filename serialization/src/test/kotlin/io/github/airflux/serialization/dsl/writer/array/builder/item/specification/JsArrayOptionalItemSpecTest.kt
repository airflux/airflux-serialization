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

package io.github.airflux.serialization.dsl.writer.array.builder.item.specification

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayOptionalItemSpecTest : FreeSpec() {

    companion object {
        private const val ITEM_VALUE = "value"

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsArrayItemSpec#Optional" - {

            "when created the instance of a spec of the optional item" - {
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = optional(writer = writer)

                "then the instance should contain the writer passed during initialization" {
                    spec.writer shouldBe writer
                }
            }

            "when the filter was added to the spec" - {
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = optional(writer = writer)
                val specWithFilter = spec.filter { _, _, value -> value.isNotEmpty() }

                "when passing a value that satisfies the predicate for filtering" - {
                    val result = specWithFilter.writer.write(CONTEXT, LOCATION, ITEM_VALUE)

                    "then the not null value should be returned" {
                        result shouldBe StringNode(ITEM_VALUE)
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
