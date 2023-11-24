/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.std.validator.number

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid

public class MaximumNumberValidator<EB, O, T> internal constructor(
    private val expected: T
) : JsValidator<EB, O, T>
    where EB : MaximumNumberValidator.ErrorBuilder,
          T : Number,
          T : Comparable<T> {

    override fun validate(
        env: JsReaderEnv<EB, O>,
        context: JsContext,
        location: JsLocation,
        value: T
    ): ValidationResult =
        if (value <= expected)
            valid()
        else
            invalid(location = location, error = env.errorBuilders.maximumNumberError(expected, value))

    public fun interface ErrorBuilder {
        public fun maximumNumberError(expected: Number, actual: Number): JsReaderResult.Error
    }
}
