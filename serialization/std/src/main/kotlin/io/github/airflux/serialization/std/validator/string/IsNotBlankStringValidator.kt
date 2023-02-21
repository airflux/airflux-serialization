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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator

public class IsNotBlankStringValidator<EB, O, CTX> internal constructor() : Validator<EB, O, CTX, String>
    where EB : IsNotBlankStringValidator.ErrorBuilder {

    override fun validate(
        env: ReaderEnv<EB, O>,
        context: CTX,
        location: Location,
        value: String
    ): ReaderResult.Failure? =
        if (value.isNotBlank())
            null
        else
            ReaderResult.Failure(location = location, error = env.errorBuilders.isNotBlankStringError())

    public interface ErrorBuilder {
        public fun isNotBlankStringError(): ReaderResult.Error
    }
}