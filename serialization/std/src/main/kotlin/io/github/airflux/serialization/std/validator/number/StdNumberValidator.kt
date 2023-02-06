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

import io.github.airflux.serialization.core.reader.validator.Validator

public object StdNumberValidator {

    /**
     * Validation of a value, if a value is less than an expected [value] then an error,
     * otherwise a success.
     */
    public fun <EB, O, CTX, T> minimum(value: T): Validator<EB, O, CTX, T>
        where EB : MinimumNumberValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = MinimumNumberValidator(value)

    /**
     * Validation of a value, if a value is greater than an expected [value] then error,
     * otherwise a success.
     */
    public fun <EB, O, CTX, T> maximum(value: T): Validator<EB, O, CTX, T>
        where EB : MaximumNumberValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = MaximumNumberValidator(value)

    /**
     * Validation of a value, if a value is less than or equal to an expected [value] then an error,
     * otherwise a success.
     */
    public fun <EB, O, CTX, T> exclusiveMinimum(value: T): Validator<EB, O, CTX, T>
        where EB : ExclusiveMinimumNumberValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = ExclusiveMinimumNumberValidator(value)

    /**
     * Validation of a value, if a value is greater than or equal to an expected [value] then error,
     * otherwise a success.
     */
    public fun <EB, O, CTX, T> exclusiveMaximum(value: T): Validator<EB, O, CTX, T>
        where EB : ExclusiveMaximumNumberValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = ExclusiveMaximumNumberValidator(value)
}
