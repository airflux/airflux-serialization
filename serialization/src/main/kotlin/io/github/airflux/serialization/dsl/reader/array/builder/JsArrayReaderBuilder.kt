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

package io.github.airflux.serialization.dsl.reader.array.builder

import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsArrayReader
import io.github.airflux.serialization.core.reader.array.readArray
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.failFast
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.JsArrayItemSpec
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.JsArrayPrefixItemsSpec
import io.github.airflux.serialization.dsl.reader.array.builder.validator.JsArrayReaderValidatorsBuilder
import io.github.airflux.serialization.dsl.reader.array.builder.validator.JsArrayReaderValidatorsBuilderInstance
import io.github.airflux.serialization.dsl.reader.array.builder.validator.JsArrayValidators
import io.github.airflux.serialization.dsl.reader.config.JsArrayReaderConfig

public fun <T> arrayReader(
    configuration: JsArrayReaderConfig = JsArrayReaderConfig.DEFAULT,
    block: JsArrayReaderBuilder<T>.() -> ResultBuilder<T>
): JsArrayReader<T> {
    val readerBuilder: JsArrayReaderBuilder<T> =
        JsArrayReaderBuilder(JsArrayReaderValidatorsBuilderInstance(configuration))
    val resultBuilder: ResultBuilder<T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class JsArrayReaderBuilder<T> internal constructor(
    private val validatorsBuilder: JsArrayReaderValidatorsBuilderInstance
) : JsArrayReaderValidatorsBuilder by validatorsBuilder {

    public fun interface ResultBuilder<T> {
        public fun build(context: JsReaderContext, location: JsLocation, input: JsArray<*>): JsResult<List<T>>
    }

    internal fun build(resultBuilder: ResultBuilder<T>): JsArrayReader<T> {
        val validators = validatorsBuilder.build()
        return buildObjectReader(validators, resultBuilder)
    }
}

public fun <T> returns(items: JsArrayItemSpec<T>): ResultBuilder<T> =
    ResultBuilder { context, location, input ->
        readArray(context = context, location = location, from = input, items = items.reader)
    }

public fun <T> returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: Boolean): ResultBuilder<T> {
    val prefixItemReaders = prefixItems.readers
    return ResultBuilder { context, location, input ->
        readArray(
            context = context,
            location = location,
            from = input,
            prefixItems = prefixItemReaders,
            errorIfAdditionalItems = !items
        )
    }
}

public fun <T> returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: JsArrayItemSpec<T>): ResultBuilder<T> {
    val prefixItemReaders = prefixItems.readers
    return ResultBuilder { context, location, input ->
        readArray(
            context = context,
            location = location,
            from = input,
            prefixItems = prefixItemReaders,
            items = items.reader
        )
    }
}

internal fun <T> buildObjectReader(
    validators: JsArrayValidators,
    resultBuilder: ResultBuilder<T>
): JsArrayReader<T> =
    JsArrayReader { context, location, input ->
        if (input !is JsArray<*>) {
            val errorBuilder = context[InvalidTypeErrorBuilder]
            return@JsArrayReader JsResult.Failure(
                location = location,
                error = errorBuilder.build(JsValue.Type.ARRAY, input.type)
            )
        }

        val failures = mutableListOf<JsResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(context, location, input)
            if (failure != null) {
                if (context.failFast) return@JsArrayReader failure
                failures.add(failure)
            }
        }

        resultBuilder.build(context, location, input)
            .fold(
                ifFailure = { failure ->
                    if (context.failFast) return@JsArrayReader failure
                    failures.add(failure)
                    failures.merge()
                },
                ifSuccess = { success ->
                    if (failures.isNotEmpty()) failures.merge() else success
                }
            )
    }
