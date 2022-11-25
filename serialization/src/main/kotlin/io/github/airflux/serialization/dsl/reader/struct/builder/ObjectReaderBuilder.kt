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
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.struct.builder.ObjectReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectReaderPropertiesBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectReaderPropertiesBuilderInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectReaderValidation
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectReaderValidationInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidator

public fun <EB, CTX, T> structReader(block: ObjectReaderBuilder<EB, CTX, T>.() -> ResultBuilder<EB, CTX, T>): Reader<EB, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {
    val readerBuilder = ObjectReaderBuilder<EB, CTX, T>(
        ObjectReaderPropertiesBuilderInstance(),
        ObjectReaderValidationInstance()
    )
    val resultBuilder: ResultBuilder<EB, CTX, T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class ObjectReaderBuilder<EB, CTX, T> internal constructor(
    private val propertiesBuilder: ObjectReaderPropertiesBuilderInstance<EB, CTX>,
    private val validatorsBuilder: ObjectReaderValidationInstance<EB, CTX>
) : ObjectReaderPropertiesBuilder<EB, CTX> by propertiesBuilder,
    ObjectReaderValidation<EB, CTX> by validatorsBuilder
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
        val properties: ObjectProperties<EB, CTX> = propertiesBuilder.build()
        val validators: List<ObjectValidator<EB, CTX>> = validatorsBuilder.build(properties)
        return ObjectReader(validators, properties, resultBuilder)
    }
}

public fun <EB, CTX, T> returns(
    builder: PropertyValues<EB, CTX>.(ReaderEnv<EB, CTX>, Location) -> ReaderResult<T>
): ResultBuilder<EB, CTX, T> =
    ResultBuilder { env, location, values -> values.builder(env, location) }

internal class ObjectReader<EB, CTX, T>(
    private val validators: List<ObjectValidator<EB, CTX>>,
    private val properties: ObjectProperties<EB, CTX>,
    private val resultBuilder: ResultBuilder<EB, CTX, T>
) : Reader<EB, CTX, T>
    where EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {

    override fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ValueNode): ReaderResult<T> =
        if (source is ObjectNode)
            read(env, location, source)
        else
            ReaderResult.Failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(ValueNode.Type.OBJECT, source.type)
            )

    private fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ObjectNode): ReaderResult<T> {
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

        fun <EB, CTX> ObjectNode.read(
            env: ReaderEnv<EB, CTX>,
            location: Location,
            property: ObjectProperty<EB, CTX>
        ): ReaderResult<Any?> {
            val reader = when (property) {
                is ObjectProperty.Required<EB, CTX, *> -> property.reader
                is ObjectProperty.Defaultable<EB, CTX, *> -> property.reader
                is ObjectProperty.Optional<EB, CTX, *> -> property.reader
                is ObjectProperty.OptionalWithDefault<EB, CTX, *> -> property.reader
                is ObjectProperty.Nullable<EB, CTX, *> -> property.reader
                is ObjectProperty.NullableWithDefault<EB, CTX, *> -> property.reader
            }
            return reader.read(env, location, this)
        }
    }
}
