/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.reader.array

import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder

public interface ArrayReader<EB, O, T> : JsReader<EB, O, List<T>>

public fun <EB, O, T> arrayReader(block: ArrayReaderBuilder<EB, O>.() -> ArrayReader<EB, O, T>): ArrayReader<EB, O, T>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {
    val readerBuilder = ArrayReaderBuilder<EB, O>()
    return block(readerBuilder)
}

public fun <EB, O, T> ArrayReaderBuilder<EB, O>.returns(
    items: JsReader<EB, O, T>
): ArrayReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(items)

public fun <EB, O, T> ArrayReaderBuilder<EB, O>.returns(
    prefixItems: ArrayPrefixItems<EB, O, T>,
    items: Boolean
): ArrayReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(prefixItems, items)

public fun <EB, O, T> ArrayReaderBuilder<EB, O>.returns(
    prefixItems: ArrayPrefixItems<EB, O, T>,
    items: JsReader<EB, O, T>
): ArrayReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(prefixItems, items)
