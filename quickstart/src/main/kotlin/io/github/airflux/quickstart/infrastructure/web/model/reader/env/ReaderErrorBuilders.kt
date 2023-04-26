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

package io.github.airflux.quickstart.infrastructure.web.model.reader.env

import io.github.airflux.quickstart.infrastructure.web.error.JsonErrors
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.std.validator.array.IsNotEmptyArrayValidator
import io.github.airflux.serialization.std.validator.number.ExclusiveMinimumNumberValidator
import io.github.airflux.serialization.std.validator.string.IsNotBlankStringValidator
import io.github.airflux.serialization.std.validator.struct.AdditionalPropertiesStructValidator
import io.github.airflux.serialization.std.validator.struct.IsNotEmptyStructValidator

object ReaderErrorBuilders : InvalidTypeErrorBuilder,
                             PathMissingErrorBuilder,
                             IsNotBlankStringValidator.ErrorBuilder,
                             IsNotEmptyStructValidator.ErrorBuilder,
                             AdditionalPropertiesStructValidator.ErrorBuilder,
                             ExclusiveMinimumNumberValidator.ErrorBuilder,
                             AdditionalItemsErrorBuilder,
                             IsNotEmptyArrayValidator.ErrorBuilder {
    override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
        JsonErrors.InvalidType(expected = expected, actual = actual)

    override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing

    override fun isNotBlankStringError(): ReadingResult.Error = JsonErrors.Validation.Strings.IsBlank

    override fun isNotEmptyStructError(): ReadingResult.Error = JsonErrors.Validation.Struct.IsEmpty

    override fun additionalPropertiesStructError(): ReadingResult.Error =
        JsonErrors.Validation.Struct.AdditionalProperties

    override fun exclusiveMinimumNumberError(expected: Number, actual: Number): ReadingResult.Error =
        JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual)

    override fun additionalItemsError(): ReadingResult.Error = JsonErrors.AdditionalItems

    override fun isNotEmptyArrayError(): ReadingResult.Error = JsonErrors.Validation.Arrays.IsEmpty
}
