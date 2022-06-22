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

package io.github.airflux.std.validator.string

import io.github.airflux.core.reader.validator.JsValidator

public object StringValidator {
    public val isNotEmpty: JsValidator<String> = IsNotEmptyStringValidator()
    public val isNotBlank: JsValidator<String> = IsNotBlankStringValidator()
    public fun pattern(pattern: Regex): JsValidator<String> = PatternStringValidator(pattern)
    public fun minLength(value: Int): JsValidator<String> = MinLengthStringValidator(value)
    public fun maxLength(value: Int): JsValidator<String> = MaxLengthStringValidator(value)
    public fun isA(predicate: (String) -> Boolean): JsValidator<String> = IsAStringValidator(predicate)
}