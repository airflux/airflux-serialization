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

import io.github.airflux.serialization.core.reader.validation.JsValidator

public object StdStringValidator {
    public fun <EB, O, CTX> isNotEmpty(): JsValidator<EB, O, CTX, String?>
        where EB : IsNotEmptyStringValidator.ErrorBuilder = IsNotEmptyStringValidator()

    public fun <EB, O, CTX> isNotBlank(): JsValidator<EB, O, CTX, String?>
        where EB : IsNotBlankStringValidator.ErrorBuilder = IsNotBlankStringValidator()

    public fun <EB, O, CTX> pattern(pattern: Regex): JsValidator<EB, O, CTX, String?>
        where EB : PatternStringValidator.ErrorBuilder = PatternStringValidator(pattern)

    public fun <EB, O, CTX> minLength(value: Int): JsValidator<EB, O, CTX, String?>
        where EB : MinLengthStringValidator.ErrorBuilder = MinLengthStringValidator(value)

    public fun <EB, O, CTX> maxLength(value: Int): JsValidator<EB, O, CTX, String?>
        where EB : MaxLengthStringValidator.ErrorBuilder = MaxLengthStringValidator(value)

    public fun <EB, O, CTX> isA(predicate: (String) -> Boolean): JsValidator<EB, O, CTX, String>
        where EB : IsAStringValidator.ErrorBuilder = IsAStringValidator(predicate)
}
