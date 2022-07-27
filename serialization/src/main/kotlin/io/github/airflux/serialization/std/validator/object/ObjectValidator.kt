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

package io.github.airflux.serialization.std.validator.`object`

import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.std.AdditionalPropertiesObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.std.IsNotEmptyObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.std.MaxPropertiesObjectValidatorBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.std.MinPropertiesObjectValidatorBuilder

public object ObjectValidator {
    public val additionalProperties: JsObjectValidatorBuilder = AdditionalPropertiesObjectValidatorBuilder()
    public val isNotEmpty: JsObjectValidatorBuilder = IsNotEmptyObjectValidatorBuilder()
    public fun minProperties(value: Int): JsObjectValidatorBuilder = MinPropertiesObjectValidatorBuilder(value)
    public fun maxProperties(value: Int): JsObjectValidatorBuilder = MaxPropertiesObjectValidatorBuilder(value)
}
