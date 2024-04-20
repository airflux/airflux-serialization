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

package io.github.airflux.serialization.dsl.reader.array

import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.array.validation.JsArrayValidator

@AirfluxMarker
public class JsArrayReaderBuilder<EB, O>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    private var validatorBuilder: (() -> JsArrayValidator<EB, O>)? = null

    public fun validation(validator: () -> JsArrayValidator<EB, O>) {
        validatorBuilder = validator
    }

    public fun validation(validator: JsArrayValidator.Builder<EB, O>) {
        validatorBuilder = { validator.build() }
    }

    public fun validation(validator: JsArrayValidator<EB, O>) {
        validatorBuilder = { validator }
    }

    public fun <T> build(items: JsReader<EB, O, T>): JsArrayReader<EB, O, T> =
        ItemsArrayReader(items).addValidator(validatorBuilder)

    public fun <T> build(prefixItems: ArrayPrefixItems<EB, O, T>, items: Boolean): JsArrayReader<EB, O, T> =
        ItemsArrayReader(prefixItems, items).addValidator(validatorBuilder)

    public fun <T> build(prefixItems: ArrayPrefixItems<EB, O, T>, items: JsReader<EB, O, T>): JsArrayReader<EB, O, T> =
        ItemsArrayReader(prefixItems, items).addValidator(validatorBuilder)

    private fun <T> ItemsArrayReader<EB, O, T>.addValidator(
        builder: (() -> JsArrayValidator<EB, O>)?
    ): JsArrayReader<EB, O, T> =
        if (builder != null) ArrayReaderWithValidation(builder(), this) else this
}
