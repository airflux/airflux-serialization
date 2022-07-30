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

package io.github.airflux.serialization.dsl.reader.array.builder.item.specification

import io.github.airflux.serialization.std.reader.IntReader
import io.github.airflux.serialization.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class ArrayPrefixItemsSpecTest : FreeSpec() {

    init {

        "The ArrayPrefixItemsSpec type" - {

            "when creating the prefix items spec for array reader" - {
                val first = nullable(StringReader)
                val second = nullable(IntReader)
                val specs = prefixItems(first, second)

                "then it should have elements in the order they were passed element" {
                    specs.readers shouldContainExactly listOf(first.reader, second.reader)
                }
            }
        }
    }
}
