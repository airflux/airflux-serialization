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

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.struct.property.PropertyValues
import io.github.airflux.serialization.core.reader.struct.property.PropertyValuesInstance
import io.github.airflux.serialization.core.reader.struct.property.StructProperties
import io.github.airflux.serialization.core.reader.struct.validation.JsStructValidator
import io.github.airflux.serialization.core.reader.validation.getOrNull
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue

public fun <EB, O, T> buildStructReader(
    properties: StructProperties<EB, O>,
    typeBuilder: JsStructTypeBuilder<EB, O, T>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption =
    object : AbstractStructReader<EB, O, T>(properties) {
        override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T> =
            readStructProperties(env, location, source, properties)
                .fold(
                    onSuccess = { result -> typeBuilder(result.value, env, location) },
                    onFailure = { it }
                )
    }

public fun <EB, O, T> buildStructReader(
    properties: StructProperties<EB, O>,
    validator: JsStructValidator<EB, O>,
    typeBuilder: JsStructTypeBuilder<EB, O, T>
): JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption =
    object : AbstractStructReader<EB, O, T>(properties) {
        override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T> {
            val failureAccumulator: JsReaderResult.Failure? =
                validator.validate(env, location, properties, source).getOrNull()
            return if (failureAccumulator != null && env.config.options.failFast)
                failureAccumulator
            else
                readStructProperties(env, location, source, properties)
                    .fold(
                        onFailure = { failure -> failureAccumulator + failure },
                        onSuccess = {
                            failureAccumulator ?: typeBuilder(it.value, env, location)
                        }
                    )
        }
    }

private abstract class AbstractStructReader<EB, O, T>(
    override val properties: StructProperties<EB, O>
) : JsStructReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder {

    final override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue): JsReaderResult<T> =
        if (source is JsStruct)
            read(env, location, source)
        else
            failure(
                location = location,
                error = env.config.errorBuilders.invalidTypeError(expected = JsValue.Type.STRUCT, actual = source.type)
            )

    abstract fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T>

    protected fun <EB, O> readStructProperties(
        env: JsReaderEnv<EB, O>,
        location: JsLocation,
        source: JsStruct,
        properties: StructProperties<EB, O>
    ): JsReaderResult<PropertyValues<EB, O>>
        where EB : InvalidTypeErrorBuilder,
              O : FailFastOption {
        val failFast = env.config.options.failFast
        var failureAccumulator: JsReaderResult.Failure? = null

        val values = PropertyValuesInstance<EB, O>()
            .apply {
                properties.forEach { property ->
                    property.read(env, location, source)
                        .fold(
                            onFailure = { failure ->
                                if (failFast) return failure
                                failureAccumulator = failureAccumulator + failure
                            },
                            onSuccess = { success ->
                                this[property] = success.value
                            }
                        )
                }
            }

        return failureAccumulator ?: success(location, values)
    }
}
