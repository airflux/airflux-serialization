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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidatorBuilder
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidators

public fun <EB, O, CTX, T> structReader(
    block: StructReader.Builder<EB, O, CTX, T>.() -> Reader<EB, O, CTX, T>
): Reader<EB, O, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {
    val builder = StructReader.Builder<EB, O, CTX, T>()
    return block(builder)
}

public fun <EB, O, CTX, T> StructReader.Builder<EB, O, CTX, T>.returns(
    block: PropertyValues<EB, O, CTX>.(ReaderEnv<EB, O>, CTX, Location) -> ReaderResult<T>
): Reader<EB, O, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption = this.build(block)

public class StructReader<EB, O, CTX, T>(
    private val validators: StructValidators<EB, O, CTX>,
    private val properties: StructProperties<EB, O, CTX>,
    private val readerResultBuilder: PropertyValues<EB, O, CTX>.(ReaderEnv<EB, O>, CTX, Location) -> ReaderResult<T>
) : Reader<EB, O, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    override fun read(env: ReaderEnv<EB, O>, context: CTX, location: Location, source: ValueNode): ReaderResult<T> =
        if (source is StructNode)
            read(env, context, location, source)
        else
            ReaderResult.Failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(StructNode.nameOfType), source.nameOfType)
            )

    private fun read(env: ReaderEnv<EB, O>, context: CTX, location: Location, source: StructNode): ReaderResult<T> {
        val failFast = env.options.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(env, context, location, properties, source)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        val propertyValues: PropertyValues<EB, O, CTX> = PropertyValuesInstance<EB, O, CTX>()
            .apply {
                properties.forEach { property ->
                    when (property) {
                        is StructProperty.NonNullable<EB, O, CTX, *> ->
                            property.reader.read(env, context, location, source)
                                .fold(
                                    ifFailure = { failure ->
                                        if (failFast) return failure
                                        failures.add(failure)
                                    },
                                    ifSuccess = { success ->
                                        this[property] = success.value
                                    }
                                )

                        is StructProperty.Nullable<EB, O, CTX, *> ->
                            property.reader.read(env, context, location, source)
                                .fold(
                                    ifFailure = { failure ->
                                        if (failFast) return failure
                                        failures.add(failure)
                                    },
                                    ifSuccess = { success ->
                                        this[property] = success.value
                                    }
                                )
                    }
                }
            }

        return if (failures.isEmpty())
            readerResultBuilder(propertyValues, env, context, location)
        else
            failures.merge()
    }

    @AirfluxMarker
    public class Builder<EB, O, CTX, T>
        where EB : InvalidTypeErrorBuilder,
              O : FailFastOption {

        private val properties = mutableListOf<StructProperty<EB, O, CTX>>()
        private val validatorBuilders = mutableListOf<StructValidatorBuilder<EB, O, CTX>>()

        public fun validation(
            validator: StructValidatorBuilder<EB, O, CTX>,
            vararg validators: StructValidatorBuilder<EB, O, CTX>
        ) {
            validation(
                validators = mutableListOf<StructValidatorBuilder<EB, O, CTX>>()
                    .apply {
                        add(validator)
                        addAll(validators)
                    }
            )
        }

        public fun validation(validators: List<StructValidatorBuilder<EB, O, CTX>>) {
            validatorBuilders.addAll(validators)
        }

        public fun <P : Any> property(
            spec: StructPropertySpec.NonNullable<EB, O, CTX, P>
        ): StructProperty.NonNullable<EB, O, CTX, P> =
            StructProperty.NonNullable(spec)
                .also { properties.add(it) }

        public fun <P : Any> property(
            spec: StructPropertySpec.Nullable<EB, O, CTX, P>
        ): StructProperty.Nullable<EB, O, CTX, P> =
            StructProperty.Nullable(spec)
                .also { properties.add(it) }

        public fun build(
            block: PropertyValues<EB, O, CTX>.(ReaderEnv<EB, O>, CTX, Location) -> ReaderResult<T>
        ): Reader<EB, O, CTX, T> {
            val validators: StructValidators<EB, O, CTX> =
                validatorBuilders.map { validatorBuilder -> validatorBuilder.build(properties) }
                    .takeIf { it.isNotEmpty() }
                    .orEmpty()
            return StructReader(validators, properties, block)
        }
    }
}
