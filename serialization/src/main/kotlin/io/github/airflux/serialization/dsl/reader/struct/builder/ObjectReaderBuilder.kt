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

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.failFast
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.config.ObjectReaderConfig
import io.github.airflux.serialization.dsl.reader.struct.builder.ObjectReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectReaderPropertiesBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectReaderPropertiesBuilderInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValues
import io.github.airflux.serialization.dsl.reader.struct.builder.property.PropertyValuesInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectReaderValidatorsBuilder
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectReaderValidatorsBuilderInstance
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidators

public fun <T> structReader(
    configuration: ObjectReaderConfig = ObjectReaderConfig.DEFAULT,
    block: ObjectReaderBuilder<T>.() -> ResultBuilder<T>
): Reader<T> {
    val readerBuilder = ObjectReaderBuilder<T>(
        ObjectReaderPropertiesBuilderInstance(),
        ObjectReaderValidatorsBuilderInstance(configuration)
    )
    val resultBuilder: ResultBuilder<T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class ObjectReaderBuilder<T> internal constructor(
    private val propertiesBuilder: ObjectReaderPropertiesBuilderInstance,
    private val validatorsBuilder: ObjectReaderValidatorsBuilderInstance
) : ObjectReaderPropertiesBuilder by propertiesBuilder,
    ObjectReaderValidatorsBuilder by validatorsBuilder {

    public fun interface ResultBuilder<T> {
        public fun build(context: ReaderContext, location: Location, propertyValues: PropertyValues): ReaderResult<T>
    }

    internal fun build(resultBuilder: ResultBuilder<T>): Reader<T> {
        val properties: ObjectProperties = propertiesBuilder.build()
        val validators: ObjectValidators = validatorsBuilder.build(properties)
        return ObjectReader(validators, properties, resultBuilder)
    }
}

public fun <T> returns(builder: PropertyValues.(ReaderContext, Location) -> ReaderResult<T>): ResultBuilder<T> =
    ResultBuilder { context, location, values ->
        values.builder(context, location)
    }

internal class ObjectReader<T>(
    private val validators: ObjectValidators,
    private val properties: ObjectProperties,
    private val resultBuilder: ResultBuilder<T>
) : Reader<T> {

    override fun read(context: ReaderContext, location: Location, source: ValueNode): ReaderResult<T> =
        if (source is ObjectNode)
            read(context, location, source)
        else {
            val errorBuilder = context[InvalidTypeErrorBuilder]
            ReaderResult.Failure(
                location = location,
                error = errorBuilder.build(ValueNode.Type.OBJECT, source.type)
            )
        }

    private fun read(context: ReaderContext, location: Location, source: ObjectNode): ReaderResult<T> {
        val failFast = context.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(context, location, properties, source)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        val propertyValues: PropertyValues = PropertyValuesInstance()
            .apply {
                properties.forEach { property ->
                    source.read(context, location, property)
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
            resultBuilder.build(context, location, propertyValues)
        else
            failures.merge()
    }

    internal companion object {

        fun ObjectNode.read(
            context: ReaderContext,
            location: Location,
            property: ObjectProperty
        ): ReaderResult<Any?> {
            val reader = when (property) {
                is ObjectProperty.Required<*> -> property.reader
                is ObjectProperty.Defaultable<*> -> property.reader
                is ObjectProperty.Optional<*> -> property.reader
                is ObjectProperty.OptionalWithDefault<*> -> property.reader
                is ObjectProperty.Nullable<*> -> property.reader
                is ObjectProperty.NullableWithDefault<*> -> property.reader
            }
            return reader.read(context, location, this)
        }
    }
}
