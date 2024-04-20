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
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.getOrNull
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validation.JsStructValidator

internal class StructReaderWithValidation<EB, O, T>(
    private val validator: JsStructValidator<EB, O>,
    private val reader: JsStructReader<EB, O, T>,
) : AbstractStructReader<EB, O, T>()
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    override val properties: StructProperties<EB, O>
        get() = reader.properties

    override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T> {
        val failureAccumulator: JsReaderResult.Failure? = source.validate(env, location).getOrNull()
        return if (failureAccumulator != null && env.options.failFast)
            failureAccumulator
        else
            reader.read(env, location, source)
                .fold(
                    onFailure = { failure -> failureAccumulator + failure },
                    onSuccess = { failureAccumulator ?: it }
                )
    }

    private fun JsStruct.validate(env: JsReaderEnv<EB, O>, location: JsLocation) =
        validator.validate(env, location, properties, this)
}
