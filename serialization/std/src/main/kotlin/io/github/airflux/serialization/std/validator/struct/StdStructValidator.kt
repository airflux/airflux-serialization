/*
 * Copyright 2021-2024 Maxim Sambulat.
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
import io.github.airflux.serialization.core.reader.struct.property.JsStructProperties
import io.github.airflux.serialization.core.reader.struct.validation.JsStructValidator

public object StdStructValidator {

    @JvmStatic
    public fun <EB, O> additionalProperties(properties: JsStructProperties<EB, O>): JsStructValidator<EB, O>
        where EB : AdditionalPropertiesStructValidator.ErrorBuilder,
              O : FailFastOption =
        AdditionalPropertiesStructValidator(properties)

    @JvmStatic
    public fun <EB, O> isNotEmpty(): JsStructValidator<EB, O>
        where EB : IsNotEmptyStructValidator.ErrorBuilder =
        IsNotEmptyStructValidator()

    @JvmStatic
    public fun <EB, O> minProperties(value: Int): JsStructValidator<EB, O>
        where EB : MinPropertiesStructValidator.ErrorBuilder =
        MinPropertiesStructValidator(value)

    @JvmStatic
    public fun <EB, O> maxProperties(value: Int): JsStructValidator<EB, O>
        where EB : MaxPropertiesStructValidator.ErrorBuilder =
        MaxPropertiesStructValidator(value)
}
