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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.validator.Validator

public fun <T> ReaderResult<T?>.filter(
    context: ReaderContext,
    location: Location,
    predicate: ReaderPredicate<T>
): ReaderResult<T?> =
    fold(
        ifFailure = ::identity,
        ifSuccess = { result ->
            if (result.value == null)
                result
            else {
                if (predicate.test(context, location, result.value))
                    result
                else
                    ReaderResult.Success(null)
            }
        }
    )

public fun <T> ReaderResult<T>.validation(
    context: ReaderContext,
    location: Location,
    validator: Validator<T>
): ReaderResult<T> =
    fold(
        ifFailure = ::identity,
        ifSuccess = { result -> validator.validate(context, location, result.value) ?: result }
    )
