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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.array.validation.JsArrayValidator
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsArray

public class MaxItemsArrayValidator<EB, O> internal constructor(
    private val expected: Int
) : JsArrayValidator<EB, O>
    where EB : MaxItemsArrayValidator.ErrorBuilder {

    override fun validate(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsArray): JsValidatorResult =
        if (source.size > expected)
            invalid(location, env.config.errorBuilders.maxItemsArrayError(expected, source.size))
        else
            valid()

    public fun interface ErrorBuilder {
        public fun maxItemsArrayError(expected: Int, actual: Int): JsReaderResult.Error
    }
}
