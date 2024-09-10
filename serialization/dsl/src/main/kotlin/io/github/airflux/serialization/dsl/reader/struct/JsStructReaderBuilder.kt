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

package io.github.airflux.serialization.dsl.reader.struct

import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.dsl.reader.struct.validation.JsStructValidator
import io.github.airflux.serialization.dsl.reader.struct.validation.JsStructValidatorBuilder

public inline fun <EB, O, T> structReader(
    block: JsStructReaderBuilder<EB, O>.() -> JsStructReader<EB, O, T>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = JsStructReaderBuilder<EB, O>().block()

public fun <EB, O, T> JsStructReaderBuilder<EB, O>.returns(
    block: JsStructTypeBuilder<EB, O, T>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = build(block)

@AirfluxMarker
public class JsStructReaderBuilder<EB, O>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    private val properties = mutableListOf<StructProperty<EB, O, *>>()
    private var validatorBuilder: JsStructValidatorBuilder<EB, O>? = null

    public fun validation(validator: JsStructValidator<EB, O>) {
        validation { validator }
    }

    public fun validation(validator: JsStructValidatorBuilder<EB, O>) {
        validatorBuilder = validator
    }

    public fun <P> property(spec: StructPropertySpec<EB, O, P>): StructProperty<EB, O, P> =
        StructProperty(spec).also { properties.add(it) }

    public fun <T> build(block: JsStructTypeBuilder<EB, O, T>): JsStructReader<EB, O, T> {
        val validator = validatorBuilder?.invoke(properties)
        return if (validator == null)
            buildStructReader(properties, block)
        else
            buildStructReader(properties, validator, block)
    }
}
