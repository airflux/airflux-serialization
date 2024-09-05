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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.readItems
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsArray

internal class ItemsArrayReader<EB, O, T> private constructor(
    private val typeBuilder: (JsReaderEnv<EB, O>, JsLocation, JsArray) -> JsReaderResult<List<T>>
) : AbstractArrayReader<EB, O, T>()
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    constructor(items: JsReader<EB, O, T>) :
        this({ env, location, source ->
            source.readItems(env = env, location = location, itemsReader = items)
        })

    constructor(prefixItems: ArrayPrefixItems<EB, O, T>, items: Boolean) :
        this({ env, location, source ->
            source.readItems(
                env = env,
                location = location,
                prefixItemReaders = prefixItems,
                errorIfAdditionalItems = !items
            )
        })

    constructor(prefixItems: ArrayPrefixItems<EB, O, T>, items: JsReader<EB, O, T>) :
        this({ env, location, source ->
            source.readItems(
                env = env,
                location = location,
                prefixItemReaders = prefixItems,
                itemsReader = items
            )
        })

    override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsArray): JsReaderResult<List<T>> =
        typeBuilder(env, location, source)
}
