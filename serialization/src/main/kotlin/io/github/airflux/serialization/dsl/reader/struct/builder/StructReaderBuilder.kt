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
import io.github.airflux.serialization.dsl.reader.struct.builder.StructReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructReaderPropertiesBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.StructReaderPropertiesBuilderInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructReaderValidation
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructReaderValidationInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.StructValidator

public fun <EB, CTX, T> structReader(
    block: StructReaderBuilder<EB, CTX, T>.() -> ResultBuilder<EB, CTX, T>
): Reader<EB, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {
    val readerBuilder = StructReaderBuilder<EB, CTX, T>(
        StructReaderPropertiesBuilderInstance(),
        StructReaderValidationInstance()
    )
    val resultBuilder: ResultBuilder<EB, CTX, T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class StructReaderBuilder<EB, CTX, T> internal constructor(
    private val propertiesBuilder: StructReaderPropertiesBuilderInstance<EB, CTX>,
    private val validatorsBuilder: StructReaderValidationInstance<EB, CTX>
) : StructReaderPropertiesBuilder<EB, CTX> by propertiesBuilder,
    StructReaderValidation<EB, CTX> by validatorsBuilder
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {

    public fun interface ResultBuilder<EB, CTX, T> {
        public fun build(
            env: ReaderEnv<EB, CTX>,
            location: Location,
            propertyValues: PropertyValues<EB, CTX>
        ): ReaderResult<T>
    }

    internal fun build(resultBuilder: ResultBuilder<EB, CTX, T>): Reader<EB, CTX, T> {
        val properties: StructProperties<EB, CTX> = propertiesBuilder.build()
        val validators: List<StructValidator<EB, CTX>> = validatorsBuilder.build(properties)
        return StructReader(validators, properties, resultBuilder)
    }
}

public fun <EB, CTX, T> returns(
    builder: PropertyValues<EB, CTX>.(ReaderEnv<EB, CTX>, Location) -> ReaderResult<T>
): ResultBuilder<EB, CTX, T> =
    ResultBuilder { env, location, values -> values.builder(env, location) }

internal class StructReader<EB, CTX, T>(
    private val validators: List<StructValidator<EB, CTX>>,
    private val properties: StructProperties<EB, CTX>,
    private val resultBuilder: ResultBuilder<EB, CTX, T>
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
                    source.read(env, location, property)
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

        return if (failures.isEmpty())
            resultBuilder.build(env, location, propertyValues)
        else
            failures.merge()
    }

    internal companion object {

        fun <EB, CTX> StructNode.read(
            env: ReaderEnv<EB, CTX>,
            location: Location,
            property: StructProperty<EB, CTX>
        ): ReaderResult<Any?> {
            val reader = when (property) {
                is StructProperty.NonNullable<EB, CTX, *> -> property.reader
                is StructProperty.Nullable<EB, CTX, *> -> property.reader
            }
            return reader.read(env, location, this)
        }
    }
}
