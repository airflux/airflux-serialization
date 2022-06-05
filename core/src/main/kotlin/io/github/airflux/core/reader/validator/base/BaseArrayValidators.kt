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

package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.validator.JsValidator

@Suppress("unused")
public object BaseArrayValidators {

    public fun <T, C> minItems(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsValidator<C>
        where C : Collection<T> =
        JsValidator { _, _, values ->
            if (values.size < expected) JsErrors.of(error(expected, values.size)) else null
        }

    public fun <T, C> maxItems(expected: Int, error: (expected: Int, actual: Int) -> JsError): JsValidator<C>
        where C : Collection<T> =
        JsValidator { _, _, values ->
            if (values.size > expected) JsErrors.of(error(expected, values.size)) else null
        }

    public fun <T, K> isUnique(
        keySelector: (T) -> K,
        error: (index: Int, value: K) -> JsError
    ): JsValidator<Collection<T>> =
        JsValidator { context, _, values ->
            val failFast = context.failFast
            val errors = mutableListOf<JsError>()
            val unique = mutableSetOf<K>()
            values.forEachIndexed { index, item ->
                val key = keySelector(item)
                if (!unique.add(key)) errors.add(error(index, key))
                if (failFast && errors.isNotEmpty()) return@JsValidator JsErrors.of(errors)
            }
            JsErrors.of(errors)
        }
}
