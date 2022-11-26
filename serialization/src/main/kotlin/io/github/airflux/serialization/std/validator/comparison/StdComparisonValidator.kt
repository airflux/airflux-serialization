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

package io.github.airflux.serialization.std.validator.comparison

import io.github.airflux.serialization.core.reader.validator.Validator

public object StdComparisonValidator {

    /**
     * Validation of a value, if a value is less than an expected [value] then an error, otherwise a success.
     */
    public fun <EB, CTX, T> min(value: T): Validator<EB, CTX, T>
        where EB : MinComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = MinComparisonValidator(value)

    /**
     * Validation of a value, if a value is more than an expected [value] then error, otherwise a success.
     */
    public fun <EB, CTX, T> max(value: T): Validator<EB, CTX, T>
        where EB : MaxComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = MaxComparisonValidator(value)

    /**
     * Validation of a value, if a value is equal to an expected [value] then success, otherwise an error.
     */
    public fun <EB, CTX, T> eq(value: T): Validator<EB, CTX, T>
        where EB : EqComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = EqComparisonValidator(value)

    /**
     * Validation of a value, if a value is not equal to an expected [value] then a success, otherwise an error.
     */
    public fun <EB, CTX, T> ne(value: T): Validator<EB, CTX, T>
        where EB : NeComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = NeComparisonValidator(value)

    /**
     * Validation of a value, if a value is greater than an expected [value] then a success, otherwise an error.
     */
    public fun <EB, CTX, T> gt(value: T): Validator<EB, CTX, T>
        where EB : GtComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = GtComparisonValidator(value)

    /**
     * Validation of a value, if a value is greater than or equal to an expected [value] then a success, otherwise an error.
     */
    public fun <EB, CTX, T> ge(value: T): Validator<EB, CTX, T>
        where EB : GeComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = GeComparisonValidator(value)

    /**
     * Validation of a value, if a value is less than an expected [value] then a success, otherwise an error.
     */
    public fun <EB, CTX, T> lt(value: T): Validator<EB, CTX, T>
        where EB : LtComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = LtComparisonValidator(value)

    /**
     * Validation of a value, if a value is less than or equal to an expected [value] then a success, otherwise an error.
     */
    public fun <EB, CTX, T> le(value: T): Validator<EB, CTX, T>
        where EB : LeComparisonValidator.ErrorBuilder,
              T : Number,
              T : Comparable<T> = LeComparisonValidator(value)
}
