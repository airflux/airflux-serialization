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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <T> JsResult<T?>.filter(context: ReaderContext, predicate: ReaderPredicate<T>): JsResult<T?> =
    fold(
        ifFailure = ::identity,
        ifSuccess = { result ->
            if (result.value == null)
                result
            else {
                if (predicate.test(context, result.location, result.value))
                    result
                else
                    JsResult.Success(result.location, null)
            }
        }
    )

public fun <T> JsResult<T>.validation(context: ReaderContext, validator: Validator<T>): JsResult<T> =
    fold(
        ifFailure = ::identity,
        ifSuccess = { result -> validator.validate(context, result.location, result.value) ?: result }
    )
