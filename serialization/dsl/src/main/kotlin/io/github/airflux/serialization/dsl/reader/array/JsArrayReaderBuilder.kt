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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.getOrNull
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.array.validation.JsArrayValidator

public typealias JsArrayValidatorBuilder<EB, O> = () -> JsArrayValidator<EB, O>

@AirfluxMarker
public class JsArrayReaderBuilder<EB, O>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    private var validatorBuilder: JsArrayValidatorBuilder<EB, O>? = null

    public fun validation(validator: JsArrayValidator<EB, O>): Unit = validation { validator }

    public fun validation(validator: JsArrayValidatorBuilder<EB, O>) {
        validatorBuilder = validator
    }

    public fun <T> build(items: JsReader<EB, O, T>): JsArrayReader<EB, O, T> =
        build(arrayItemsReader(items), validatorBuilder)

    public fun <T> build(prefixItems: ArrayPrefixItems<EB, O, T>, items: Boolean): JsArrayReader<EB, O, T> =
        build(arrayItemsReader(prefixItems, items), validatorBuilder)

    public fun <T> build(prefixItems: ArrayPrefixItems<EB, O, T>, items: JsReader<EB, O, T>): JsArrayReader<EB, O, T> =
        build(arrayItemsReader(prefixItems, items), validatorBuilder)

    private fun <T> build(
        arrayItemsReader: JsArrayItemsReader<EB, O, T>,
        builder: (() -> JsArrayValidator<EB, O>)?
    ): JsArrayReader<EB, O, T> =
        if (builder == null)
            build(arrayItemsReader)
        else
            build(arrayItemsReader, builder())

    private fun <T> build(arrayItemsReader: JsArrayItemsReader<EB, O, T>): JsArrayReader<EB, O, T> =
        JsArrayReader { env, location, source ->
            if (source is JsArray)
                arrayItemsReader.read(env, location, source)
            else
                failure(env, location, source)
        }

    private fun <T> build(
        arrayItemsReader: JsArrayItemsReader<EB, O, T>,
        validator: JsArrayValidator<EB, O>
    ): JsArrayReader<EB, O, T> =
        JsArrayReader { env, location, source ->
            if (source is JsArray) {
                val failFast = env.config.options.failFast
                val failureAccumulator: JsReaderResult.Failure? =
                    validator.validate(env, location, source).getOrNull()
                if (failureAccumulator != null && failFast)
                    failureAccumulator
                else
                    arrayItemsReader.read(env, location, source)
                        .fold(
                            onFailure = { failure -> failureAccumulator + failure },
                            onSuccess = { success -> failureAccumulator ?: success }
                        )
            } else
                failure(env, location, source)
        }

    private companion object {

        @JvmStatic
        private fun <EB, O> failure(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue)
            where EB : InvalidTypeErrorBuilder =
            failure(
                location = location,
                error = env.config.errorBuilders.invalidTypeError(expected = JsValue.Type.ARRAY, actual = source.type)
            )
    }
}
