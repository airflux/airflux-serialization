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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.core.reader.validator.Validator

public object StdStringValidator {
    public val isNotEmpty: Validator<String> = IsNotEmptyStringValidator()
    public val isNotBlank: Validator<String> = IsNotBlankStringValidator()
    public fun pattern(pattern: Regex): Validator<String> = PatternStringValidator(pattern)
    public fun minLength(value: Int): Validator<String> = MinLengthStringValidator(value)
    public fun maxLength(value: Int): Validator<String> = MaxLengthStringValidator(value)
    public fun isA(predicate: (String) -> Boolean): Validator<String> = IsAStringValidator(predicate)
}
