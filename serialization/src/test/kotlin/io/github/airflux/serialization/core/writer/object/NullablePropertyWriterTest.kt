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

package io.github.airflux.serialization.core.writer.`object`

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class NullablePropertyWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = WriterContext()
        private val LOCATION = Location.empty
    }

    init {

        "The writeNullable function" - {
            val writer: Writer<String> = DummyWriter { StringNode(it) }

            "when a value is not null" - {
                val value = "value"

                "should return the StringNode value" {
                    val result: ValueNode? =
                        writeNullable(context = CONTEXT, location = LOCATION, using = writer, value = value)
                    result shouldBe StringNode(value)
                }
            }

            "when a value is null" - {
                val value: String? = null

                "should return the NullNode value" {
                    val result: ValueNode? =
                        writeNullable(context = CONTEXT, location = LOCATION, using = writer, value = value)
                    result shouldBe NullNode
                }
            }
        }
    }
}