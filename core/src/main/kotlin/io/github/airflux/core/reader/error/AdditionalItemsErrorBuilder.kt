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

package io.github.airflux.core.reader.error

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.contextKeyName
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.result.JsError

/**
 * The builder of an error that occurs when an element appears in the array that is not described in prefixItems.
 */
public class AdditionalItemsErrorBuilder(private val builder: () -> JsError) :
    AbstractErrorBuilderContextElement<AdditionalItemsErrorBuilder>(key = AdditionalItemsErrorBuilder) {

    public fun build(): JsError = builder()

    public companion object Key : JsReaderContext.Key<AdditionalItemsErrorBuilder> {
        override val name: String = contextKeyName()
    }
}
