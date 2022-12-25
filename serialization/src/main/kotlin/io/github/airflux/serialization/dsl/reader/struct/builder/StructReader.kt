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

package io.github.airflux.serialization.dsl.reader.struct.builder

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
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.StructPropertySpec
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructValidatorBuilder

public fun <EB, CTX, T> structReader(
    block: StructReader.Builder<EB, CTX, T>.() -> Reader<EB, CTX, T>
): Reader<EB, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {
    val builder = StructReader.Builder<EB, CTX, T>()
    return block(builder)
}

public fun <EB, CTX, T> StructReader.Builder<EB, CTX, T>.returns(
    block: PropertyValues<EB, CTX>.(ReaderEnv<EB, CTX>, Location) -> ReaderResult<T>
): Reader<EB, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption = this.build(block)

public class StructReader<EB, CTX, T>(
    private val validators: List<StructValidator<EB, CTX>>,
    private val properties: StructProperties<EB, CTX>,
    private val readerResultBuilder: PropertyValues<EB, CTX>.(ReaderEnv<EB, CTX>, Location) -> ReaderResult<T>
) : Reader<EB, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {

    override fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ValueNode): ReaderResult<T> =
        if (source is StructNode)
            read(env, location, source)
        else
            ReaderResult.Failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(StructNode.nameOfType), source.nameOfType)
            )

    private fun read(env: ReaderEnv<EB, CTX>, location: Location, source: StructNode): ReaderResult<T> {
        val failFast = env.context.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(env, location, properties, source)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        val propertyValues: PropertyValues<EB, CTX> = PropertyValuesInstance<EB, CTX>()
            .apply {
                properties.forEach { property ->
                    when (property) {
                        is StructProperty.NonNullable<EB, CTX, *> -> property.reader.read(env, location, source)
                            .fold(
                                ifFailure = { failure ->
                                    if (failFast) return failure
                                    failures.add(failure)
                                },
                                ifSuccess = { success ->
                                    this[property] = success.value
                                }
                            )

                        is StructProperty.Nullable<EB, CTX, *> -> property.reader.read(env, location, source)
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
            readerResultBuilder(propertyValues, env, location)
        else
            failures.merge()
    }

    @AirfluxMarker
    public class Builder<EB, CTX, T>
        where EB : InvalidTypeErrorBuilder,
              CTX : FailFastOption {

        private val properties = mutableListOf<StructProperty<EB, CTX>>()
        private val validatorBuilders = mutableListOf<StructValidatorBuilder<EB, CTX>>()

        public fun validation(
            validator: StructValidatorBuilder<EB, CTX>,
            vararg validators: StructValidatorBuilder<EB, CTX>
        ) {
            validation(
                validators = mutableListOf<StructValidatorBuilder<EB, CTX>>()
                    .apply {
                        add(validator)
                        addAll(validators)
                    }
            )
        }

        public fun validation(validators: List<StructValidatorBuilder<EB, CTX>>) {
            validatorBuilders.addAll(validators)
        }

        public fun <P : Any> property(
            spec: StructPropertySpec.NonNullable<EB, CTX, P>
        ): StructProperty.NonNullable<EB, CTX, P> =
            StructProperty.NonNullable(spec)
                .also { properties.add(it) }

        public fun <P : Any> property(
            spec: StructPropertySpec.Nullable<EB, CTX, P>
        ): StructProperty.Nullable<EB, CTX, P> =
            StructProperty.Nullable(spec)
                .also { properties.add(it) }

        public fun build(
            block: PropertyValues<EB, CTX>.(ReaderEnv<EB, CTX>, Location) -> ReaderResult<T>
        ): Reader<EB, CTX, T> {
            val props = StructProperties(properties)
            val validators: List<StructValidator<EB, CTX>> =
                validatorBuilders.map { validatorBuilder -> validatorBuilder.build(props) }
                    .takeIf { it.isNotEmpty() }
                    .orEmpty()
            return StructReader(validators, props, block)
        }
    }
}
