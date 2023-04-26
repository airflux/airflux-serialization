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

package io.github.airflux.serialization.std.validator.property

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.Validator
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid

public class ForbiddenPropertyValidator<EB, O, CTX, T> internal constructor(
    private val predicate: (env: ReaderEnv<EB, O>, CTX, location: Location) -> Boolean
) : Validator<EB, O, CTX, T>
    where EB : ForbiddenPropertyValidator.ErrorBuilder {

    override fun validate(env: ReaderEnv<EB, O>, context: CTX, location: Location, value: T): ValidationResult =
        if (predicate(env, context, location)) {
            if (value == null)
                valid()
            else
                invalid(location = location, error = env.errorBuilders.forbiddenPropertyError())
        } else
            valid()

    public interface ErrorBuilder {
        public fun forbiddenPropertyError(): ReadingResult.Error
    }
}
