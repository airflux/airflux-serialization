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

package io.github.airflux.quickstart.dto.reader.env

import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.std.validator.array.IsNotEmptyArrayValidator
import io.github.airflux.serialization.std.validator.comparison.GtComparisonValidator
import io.github.airflux.serialization.std.validator.string.IsNotBlankStringValidator
import io.github.airflux.serialization.std.validator.struct.AdditionalPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.struct.IsNotEmptyObjectValidator
import io.github.airflux.serialization.std.validator.struct.MaxPropertiesObjectValidator
import io.github.airflux.serialization.std.validator.struct.MinPropertiesObjectValidator

object ReaderErrorBuilders : InvalidTypeErrorBuilder,
                             PathMissingErrorBuilder,
                             IsNotBlankStringValidator.ErrorBuilder,
                             IsNotEmptyObjectValidator.ErrorBuilder,
                             AdditionalPropertiesObjectValidator.ErrorBuilder,
                             MinPropertiesObjectValidator.ErrorBuilder,
                             MaxPropertiesObjectValidator.ErrorBuilder,
                             GtComparisonValidator.ErrorBuilder,
                             AdditionalItemsErrorBuilder,
                             IsNotEmptyArrayValidator.ErrorBuilder {
    override fun invalidTypeError(expected: ValueNode.Type, actual: ValueNode.Type): ReaderResult.Error =
        JsonErrors.InvalidType(expected = expected, actual = actual)

    override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing

    override fun isNotBlankStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsBlank

    override fun isNotEmptyObjectError(): ReaderResult.Error = JsonErrors.Validation.Object.IsEmpty

    override fun additionalPropertiesObjectError(): ReaderResult.Error =
        JsonErrors.Validation.Object.AdditionalProperties

    override fun minPropertiesObjectError(expected: Int, actual: Int): ReaderResult.Error =
        JsonErrors.Validation.Object.MinProperties(expected = expected, actual = actual)

    override fun maxPropertiesObjectError(expected: Int, actual: Int): ReaderResult.Error =
        JsonErrors.Validation.Object.MaxProperties(expected = expected, actual = actual)

    override fun gtComparisonError(expected: Number, actual: Number): ReaderResult.Error =
        JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual)

    override fun additionalItemsError(): ReaderResult.Error = JsonErrors.AdditionalItems

    override fun isNotEmptyArrayError(): ReaderResult.Error = JsonErrors.Validation.Arrays.IsEmpty
}
