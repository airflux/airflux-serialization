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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.dsl.reader.struct.validation.JsStructValidator

public interface JsStructReader<EB, O, T> : JsReader<EB, O, T> {
    public val properties: StructProperties<EB, O>
}

public fun <EB, O, T> structReader(block: StructReaderBuilder<EB, O>.() -> JsStructReader<EB, O, T>): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {
    val builder = StructReaderBuilder<EB, O>()
    return block(builder)
}

public fun <EB, O, T> StructReaderBuilder<EB, O>.returns(
    block: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = this.build(block)

public inline fun <EB, O, T> JsStructReader<EB, O, T>.validation(
    validator: (properties: StructProperties<EB, O>) -> JsStructValidator<EB, O>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = validation(validator(this.properties))

public fun <EB, O, T> JsStructReader<EB, O, T>.validation(
    validator: JsStructValidator.Builder<EB, O>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = validation { validator.build(it) }

public fun <EB, O, T> JsStructReader<EB, O, T>.validation(
    validator: JsStructValidator<EB, O>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = StructReaderWithValidation(validator, this)

@AirfluxMarker
public class StructReaderBuilder<EB, O>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    private val properties = mutableListOf<StructProperty<EB, O, *>>()

    public fun <P> property(spec: StructPropertySpec<EB, O, P>): StructProperty<EB, O, P> =
        StructProperty(spec).also { properties.add(it) }

    public fun <T> build(
        block: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
    ): JsStructReader<EB, O, T> = SimpleStructReader(properties, block)
}
