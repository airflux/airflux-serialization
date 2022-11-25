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
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ArrayNonNullableItemSpecTest : FreeSpec() {

    init {
        "The ArrayItemSpec#NonNullable type" - {

            "when created the instance of a spec of the non-nullable item" - {
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = nonNullable(writer = writer)

                "then the instance should contain the writer passed during initialization" {
                    spec.writer shouldBe writer
                }
            }
        }
    }
}
