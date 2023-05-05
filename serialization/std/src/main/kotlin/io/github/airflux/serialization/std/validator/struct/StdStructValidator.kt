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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidator

public object StdStructValidator {
    public fun <EB, O, CTX> additionalProperties(): StructValidator.Builder<EB, O, CTX>
        where EB : AdditionalPropertiesStructValidator.ErrorBuilder,
              O : FailFastOption =
        AdditionalPropertiesStructValidatorBuilder()

    public fun <EB, O, CTX> isNotEmpty(): StructValidator.Builder<EB, O, CTX>
        where EB : IsNotEmptyStructValidator.ErrorBuilder = IsNotEmptyStructValidatorBuilder()

    public fun <EB, O, CTX> minProperties(value: Int): StructValidator.Builder<EB, O, CTX>
        where EB : MinPropertiesStructValidator.ErrorBuilder =
        MinPropertiesStructValidatorBuilder(value)

    public fun <EB, O, CTX> maxProperties(value: Int): StructValidator.Builder<EB, O, CTX>
        where EB : MaxPropertiesStructValidator.ErrorBuilder =
        MaxPropertiesStructValidatorBuilder(value)
}
