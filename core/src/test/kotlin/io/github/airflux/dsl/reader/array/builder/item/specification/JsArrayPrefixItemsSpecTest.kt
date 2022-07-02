package io.github.airflux.dsl.reader.array.builder.item.specification

import io.github.airflux.std.reader.IntReader
import io.github.airflux.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsArrayPrefixItemsSpecTest : FreeSpec() {

    init {

        "The JsArrayPrefixItemsSpec type" - {

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
