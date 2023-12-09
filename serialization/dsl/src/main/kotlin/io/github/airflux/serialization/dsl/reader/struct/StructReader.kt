/*
 * Copyright 2021-2023 Maxim Sambulat.
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
import io.github.airflux.serialization.core.reader.validation.ifInvalid
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
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidators

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
    private val validators: StructValidators<EB, O>,
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
        private val validatorBuilders = mutableListOf<StructValidator.Builder<EB, O>>()

        public fun validation(
            validator: StructValidator.Builder<EB, O>,
            vararg validators: StructValidator.Builder<EB, O>
        ) {
            validation(
                validators = mutableListOf<StructValidator.Builder<EB, O>>()
                    .apply {
                        add(validator)
                        addAll(validators)
                    }
            )
        }

        public fun validation(validators: List<StructValidator.Builder<EB, O>>) {
            validatorBuilders.addAll(validators)
        }

        public fun <P> property(spec: StructPropertySpec<EB, O, P>): StructProperty<EB, O, P> =
            StructProperty(spec).also { properties.add(it) }

        public fun build(
            block: PropertyValues<EB, O>.(JsReaderEnv<EB, O>, JsLocation) -> JsReaderResult<T>
        ): JsReader<EB, O, T> {
            val validators: StructValidators<EB, O> =
                validatorBuilders.map { validatorBuilder -> validatorBuilder.build(properties) }
                    .takeIf { it.isNotEmpty() }
                    .orEmpty()
            return StructReader(validators, properties, block)
        }
    }

    private fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsStruct): JsReaderResult<T> {
        val failFast = env.options.failFast
        val failureAccumulator: JsReaderResult.Failure? = source.validate(env, location)
        if (failureAccumulator != null && failFast) return failureAccumulator
        return source.read(env, location)
            .fold(
                onLeft = { failure -> failureAccumulator + failure },
                onRight = { failureAccumulator ?: resultBuilder(it, env, location) }
            )
    }

    private fun JsStruct.validate(env: JsReaderEnv<EB, O>, location: JsLocation): JsReaderResult.Failure? {
        val failFast = env.options.failFast
        var failureAccumulator: JsReaderResult.Failure? = null

        validators.forEach { validator ->
            validator.validate(env, location, properties, this)
                .ifInvalid { failure ->
                    if (failFast) return failure
                    failureAccumulator += failure
                }
        }

        return failureAccumulator
    }

    private fun JsStruct.read(
        env: JsReaderEnv<EB, O>,
        location: JsLocation
    ): Either<JsReaderResult.Failure, PropertyValuesInstance<EB, O>> {
        val failFast = env.options.failFast
        var failureAccumulator: JsReaderResult.Failure? = null

        val values = PropertyValuesInstance<EB, O>()
            .apply {
                properties.forEach { property ->
                    property.read(env, location, this@read)
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
