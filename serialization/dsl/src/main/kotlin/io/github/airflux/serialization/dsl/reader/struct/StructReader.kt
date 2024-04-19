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
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.getOrNull
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.common.Either
import io.github.airflux.serialization.dsl.common.fold
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.dsl.reader.struct.validation.JsStructValidator

public fun <EB, O, T> structReader(block: StructReader.Builder<EB, O, T>.() -> JsReader<EB, O, T>): JsReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {
    val builder = StructReader.Builder<EB, O, T>()
    return block(builder)
}

public fun <EB, O, T> StructReader.Builder<EB, O, T>.returns(
    block: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
): JsReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = this.build(block)

public class StructReader<EB, O, T> private constructor(
    private val validator: JsStructValidator<EB, O>?,
    private val properties: StructProperties<EB, O>,
    private val resultBuilder: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
) : JsReader<EB, O, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue): JsReaderResult<T> =
        if (source is JsStruct)
            read(env, location, source)
        else
            failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(JsStruct.nameOfType), source.nameOfType)
            )

    @AirfluxMarker
    public class Builder<EB, O, T> internal constructor()
        where EB : InvalidTypeErrorBuilder,
              O : FailFastOption {

        private val properties = mutableListOf<StructProperty<EB, O, *>>()
        private var validatorBuilder: JsStructValidator.Builder<EB, O>? = null

        public fun validation(validator: JsStructValidator.Builder<EB, O>) {
            validatorBuilder = validator
        }

        public fun <P> property(spec: StructPropertySpec<EB, O, P>): StructProperty<EB, O, P> =
            StructProperty(spec).also { properties.add(it) }

        public fun build(
            block: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
        ): JsReader<EB, O, T> {
            val validator: JsStructValidator<EB, O>? = validatorBuilder?.build(properties)
            return StructReader(validator, properties, block)
        }
    }

    private fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T> {
        val failFast = env.options.failFast
        val failureAccumulator: JsReaderResult.Failure? = source.validate(env, location).getOrNull()
        if (failureAccumulator != null && failFast) return failureAccumulator
        return source.readProperties(env, location)
            .fold(
                onLeft = { failure -> failureAccumulator + failure },
                onRight = { failureAccumulator ?: resultBuilder(it, env, location) }
            )
    }

    private fun JsStruct.validate(env: JsReaderEnv<EB, O>, location: JsLocation) =
        validator?.validate(env, location, properties, this) ?: valid()

    private fun JsStruct.readProperties(
        env: JsReaderEnv<EB, O>,
        location: JsLocation
    ): Either<JsReaderResult.Failure, PropertyValuesInstance<EB, O>> {
        val failFast = env.options.failFast
        var failureAccumulator: JsReaderResult.Failure? = null

        val values = PropertyValuesInstance<EB, O>()
            .apply {
                properties.forEach { property ->
                    property.read(env, location, this@readProperties)
                        .fold(
                            onFailure = { failure ->
                                if (failFast) return Either.Left(failure)
                                failureAccumulator += failure
                            },
                            onSuccess = { success ->
                                this[property] = success.value
                            }
                        )
                }
            }

        return failureAccumulator?.let { Either.Left(it) } ?: Either.Right(values)
    }
}
