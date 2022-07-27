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

package io.github.airflux.serialization.dsl.reader.`object`.builder

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsObjectReader
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.failFast
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.JsObject
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandler
import io.github.airflux.serialization.dsl.reader.`object`.builder.JsObjectReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.JsObjectProperty
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.JsObjectReaderPropertiesBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.JsObjectReaderPropertiesBuilderInstance
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.JsObjectReaderValidatorsBuilder
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.JsObjectReaderValidatorsBuilderInstance
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.JsObjectValidators

public fun <T> reader(
    configuration: JsObjectReaderConfig = JsObjectReaderConfig.DEFAULT,
    block: JsObjectReaderBuilder<T>.() -> ResultBuilder<T>
): JsObjectReader<T> {
    val readerBuilder = JsObjectReaderBuilder<T>(
        JsObjectReaderPropertiesBuilderInstance(),
        JsObjectReaderValidatorsBuilderInstance(configuration)
    )
    val resultBuilder: ResultBuilder<T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class JsObjectReaderBuilder<T> internal constructor(
    private val propertiesBuilder: JsObjectReaderPropertiesBuilderInstance,
    private val validatorsBuilder: JsObjectReaderValidatorsBuilderInstance
) : JsObjectReaderPropertiesBuilder by propertiesBuilder,
    JsObjectReaderValidatorsBuilder by validatorsBuilder {

    public fun interface ResultBuilder<T> {
        public fun build(context: JsReaderContext, location: JsLocation, objectValuesMap: ObjectValuesMap): JsResult<T>
    }

    internal fun build(resultBuilder: ResultBuilder<T>): JsObjectReader<T> {
        val properties: JsObjectProperties = propertiesBuilder.build()
        val validators: JsObjectValidators = validatorsBuilder.build(properties)
        return buildObjectReader(validators, properties, resultBuilder)
    }
}

public fun <T> returns(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): ResultBuilder<T> =
    ResultBuilder { context, location, values ->
        try {
            values.builder(context, location)
        } catch (expected: Throwable) {
            val handler = context.getOrNull(ExceptionsHandler) ?: throw expected
            handler.handleException(context, location, expected)
                .failure(location)
        }
    }

internal fun <T> buildObjectReader(
    validators: JsObjectValidators,
    properties: JsObjectProperties,
    resultBuilder: ResultBuilder<T>
): JsObjectReader<T> =
    JsObjectReader { context, location, input ->
        if (input !is JsObject) {
            val errorBuilder = context[InvalidTypeErrorBuilder]
            return@JsObjectReader JsResult.Failure(
                location = location,
                error = errorBuilder.build(JsValue.Type.OBJECT, input.type)
            )
        }

        val failFast = context.failFast
        val failures = mutableListOf<JsResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(context, location, properties, input)
            if (failure != null) {
                if (failFast) return@JsObjectReader failure
                failures.add(failure)
            }
        }

        val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance()
            .apply {
                properties.forEach { property ->
                    input.read(context, location, property)
                        .fold(
                            ifFailure = { failure ->
                                if (failFast) return@JsObjectReader failure
                                failures.add(failure)
                            },
                            ifSuccess = { value ->
                                this[property] = value.value
                            }
                        )
                }
            }

        return@JsObjectReader if (failures.isEmpty())
            resultBuilder.build(context, location, objectValuesMap)
        else
            failures.merge()
    }

internal fun JsObject.read(context: JsReaderContext, location: JsLocation, property: JsObjectProperty): JsResult<Any?> {
    val reader = when (property) {
        is JsObjectProperty.Required<*> -> property.reader
        is JsObjectProperty.Defaultable<*> -> property.reader
        is JsObjectProperty.Optional<*> -> property.reader
        is JsObjectProperty.OptionalWithDefault<*> -> property.reader
        is JsObjectProperty.Nullable<*> -> property.reader
        is JsObjectProperty.NullableWithDefault<*> -> property.reader
    }
    return reader.read(context, location, this)
}
