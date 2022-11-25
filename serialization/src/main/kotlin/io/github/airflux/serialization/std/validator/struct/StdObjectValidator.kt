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
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.AdditionalPropertiesObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.IsNotEmptyObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.MaxPropertiesObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.std.MinPropertiesObjectValidatorBuilder

public object StdObjectValidator {
    public fun <EB, CTX> additionalProperties(): ObjectValidatorBuilder<EB, CTX>
        where EB : AdditionalPropertiesObjectValidator.ErrorBuilder,
              CTX : FailFastOption =
        AdditionalPropertiesObjectValidatorBuilder()

    public fun <EB, CTX> isNotEmpty(): ObjectValidatorBuilder<EB, CTX>
        where EB : IsNotEmptyObjectValidator.ErrorBuilder = IsNotEmptyObjectValidatorBuilder()

    public fun <EB, CTX> minProperties(value: Int): ObjectValidatorBuilder<EB, CTX>
        where EB : MinPropertiesObjectValidator.ErrorBuilder =
        MinPropertiesObjectValidatorBuilder(value)

    public fun <EB, CTX> maxProperties(value: Int): ObjectValidatorBuilder<EB, CTX>
        where EB : MaxPropertiesObjectValidator.ErrorBuilder =
        MaxPropertiesObjectValidatorBuilder(value)
}
