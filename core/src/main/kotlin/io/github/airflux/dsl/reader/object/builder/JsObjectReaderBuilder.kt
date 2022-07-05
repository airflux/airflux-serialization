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

package io.github.airflux.dsl.reader.`object`.builder

import io.github.airflux.core.context.error.get
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.JsObjectReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.reader.result.failure
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.dsl.reader.context.exception.ExceptionsHandler
import io.github.airflux.dsl.reader.`object`.builder.JsObjectReaderBuilder.ResultBuilder
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectReaderProperties
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectReaderPropertiesBuilder
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectReaderValidation
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectReaderValidationBuilder
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidators

public fun <T> reader(
    configuration: JsObjectReaderConfig = JsObjectReaderConfig.DEFAULT,
    block: JsObjectReaderBuilder<T>.() -> ResultBuilder<T>
): JsObjectReader<T> {
    val readerBuilder = JsObjectReaderBuilder<T>(
        JsObjectReaderPropertiesBuilder(),
        JsObjectReaderValidationBuilder(configuration)
    )
    val resultBuilder: ResultBuilder<T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class JsObjectReaderBuilder<T> internal constructor(
    private val propertiesBuilder: JsObjectReaderPropertiesBuilder,
    private val validationBuilder: JsObjectReaderValidationBuilder,
) : JsObjectReaderProperties by propertiesBuilder,
    JsObjectReaderValidation by validationBuilder {

    public fun interface ResultBuilder<T> {
        public fun build(context: JsReaderContext, location: JsLocation, objectValuesMap: ObjectValuesMap): JsResult<T>
    }

    internal fun build(resultBuilder: ResultBuilder<T>): JsObjectReader<T> {
        val properties: JsObjectProperties = propertiesBuilder.build()
        val validators: JsObjectValidators = validationBuilder.build(properties)
        return buildObjectReader(validators, properties, resultBuilder)
    }
}

public fun <T> returns(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): ResultBuilder<T> =
    ResultBuilder { context, location, values ->
        try {
            values.builder(context, location)
        } catch (expected: Throwable) {
            context.getOrNull(ExceptionsHandler)
                ?.handleException(context, location, expected)
                ?.failure(location)
                ?: throw expected
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

        if (validators.before != null) {
            val failure = validators.before.validation(context, location, properties, input)
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

        if (validators.after != null) {
            val failure =
                validators.after.validation(context, location, properties, objectValuesMap, input)
            if (failure != null) {
                if (failFast) return@JsObjectReader failure
                failures.add(failure)
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
