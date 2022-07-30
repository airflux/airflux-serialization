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

package io.github.airflux.serialization.std.validator.comparable

import io.github.airflux.serialization.core.reader.validator.Validator

public object ComparableValidator {

    /**
     * Validation of a value, if a value is less than an expected [value] then an error, otherwise a success.
     */
    public fun <T> min(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = MinComparableValidator(value)

    /**
     * Validation of a value, if a value is more than an expected [value] then error, otherwise a success.
     */
    public fun <T> max(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = MaxComparableValidator(value)

    /**
     * Validation of a value, if a value is equal to an expected [value] then success, otherwise an error.
     */
    public fun <T> eq(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = EqComparableValidator(value)

    /**
     * Validation of a value, if a value is not equal to an expected [value] then a success, otherwise an error.
     */
    public fun <T> ne(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = NeComparableValidator(value)

    /**
     * Validation of a value, if a value is greater than an expected [value] then a success, otherwise an error.
     */
    public fun <T> gt(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = GtComparableValidator(value)

    /**
     * Validation of a value, if a value is greater than or equal to an expected [value] then a success, otherwise an error.
     */
    public fun <T> ge(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = GeComparableValidator(value)

    /**
     * Validation of a value, if a value is less than an expected [value] then a success, otherwise an error.
     */
    public fun <T> lt(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = LtComparableValidator(value)

    /**
     * Validation of a value, if a value is less than or equal to an expected [value] then a success, otherwise an error.
     */
    public fun <T> le(value: T): Validator<T>
        where T : Number,
              T : Comparable<T> = LeComparableValidator(value)
}
