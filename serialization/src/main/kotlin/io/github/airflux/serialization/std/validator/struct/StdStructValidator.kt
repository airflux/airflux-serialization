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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.AdditionalPropertiesStructValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.IsNotEmptyStructValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.MaxPropertiesStructValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.MinPropertiesStructValidatorBuilder

public object StdStructValidator {
    public fun <EB, CTX> additionalProperties(): StructValidatorBuilder<EB, CTX>
        where EB : AdditionalPropertiesStructValidator.ErrorBuilder,
              CTX : FailFastOption =
        AdditionalPropertiesStructValidatorBuilder()

    public fun <EB, CTX> isNotEmpty(): StructValidatorBuilder<EB, CTX>
        where EB : IsNotEmptyStructValidator.ErrorBuilder = IsNotEmptyStructValidatorBuilder()

    public fun <EB, CTX> minProperties(value: Int): StructValidatorBuilder<EB, CTX>
        where EB : MinPropertiesStructValidator.ErrorBuilder =
        MinPropertiesStructValidatorBuilder(value)

    public fun <EB, CTX> maxProperties(value: Int): StructValidatorBuilder<EB, CTX>
        where EB : MaxPropertiesStructValidator.ErrorBuilder =
        MaxPropertiesStructValidatorBuilder(value)
}
