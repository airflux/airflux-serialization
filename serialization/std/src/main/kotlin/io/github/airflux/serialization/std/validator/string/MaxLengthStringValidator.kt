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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidationResult
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid

public class MaxLengthStringValidator<EB, O> internal constructor(
    private val expected: Int
) : JsValidator<EB, O, String?>
    where EB : MaxLengthStringValidator.ErrorBuilder {

    override fun validate(env: JsReaderEnv<EB, O>, location: JsLocation, value: String?): JsValidationResult =
        if (value != null) {
            if (value.length <= expected)
                valid()
            else
                invalid(
                    location = location,
                    error = env.errorBuilders.maxLengthStringError(expected, value.length)
                )
        } else
            valid()

    public fun interface ErrorBuilder {
        public fun maxLengthStringError(expected: Int, actual: Int): JsReaderResult.Error
    }
}
