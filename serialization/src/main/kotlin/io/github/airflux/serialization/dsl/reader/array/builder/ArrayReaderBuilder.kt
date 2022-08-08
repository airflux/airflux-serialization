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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.array.readArray
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.failFast
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.array.builder.ArrayReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.ArrayItemSpec
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.ArrayPrefixItemsSpec
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayReaderValidatorsBuilder
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayReaderValidatorsBuilderInstance
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidators
import io.github.airflux.serialization.dsl.reader.config.ArrayReaderConfig

public fun <T> arrayReader(
    configuration: ArrayReaderConfig = ArrayReaderConfig.DEFAULT,
    block: ArrayReaderBuilder<T>.() -> ResultBuilder<T>
): Reader<T> {
    val readerBuilder: ArrayReaderBuilder<T> =
        ArrayReaderBuilder(ArrayReaderValidatorsBuilderInstance(configuration))
    val resultBuilder: ResultBuilder<T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class ArrayReaderBuilder<T> internal constructor(
    private val validatorsBuilder: ArrayReaderValidatorsBuilderInstance
) : ArrayReaderValidatorsBuilder by validatorsBuilder {

    public fun interface ResultBuilder<T> {
        public fun build(context: ReaderContext, location: Location, input: ArrayNode<*>): ReaderResult<T>
    }

    internal fun build(resultBuilder: ResultBuilder<T>): Reader<T> {
        val validators = validatorsBuilder.build()
        return ArrayReader(validators, resultBuilder)
    }
}

public fun <T> returns(items: ArrayItemSpec<T>): ResultBuilder<List<T>> =
    ResultBuilder { context, location, input ->
        readArray(context = context, location = location, from = input, items = items.reader)
    }

public fun <T> returns(prefixItems: ArrayPrefixItemsSpec<T>, items: Boolean): ResultBuilder<List<T>> {
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

public fun <T> returns(prefixItems: ArrayPrefixItemsSpec<T>, items: ArrayItemSpec<T>): ResultBuilder<List<T>> {
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

internal class ArrayReader<T>(
    private val validators: ArrayValidators,
    private val resultBuilder: ResultBuilder<T>
) : Reader<T> {

    override fun read(context: ReaderContext, location: Location, input: ValueNode): ReaderResult<T> =
        if (input is ArrayNode<*>)
            read(context, location, input)
        else {
            val errorBuilder = context[InvalidTypeErrorBuilder]
            ReaderResult.Failure(
                location = location,
                error = errorBuilder.build(ValueNode.Type.ARRAY, input.type)
            )
        }

    private fun read(context: ReaderContext, location: Location, input: ArrayNode<*>): ReaderResult<T> {
        val failFast = context.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(context, location, input)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        return resultBuilder.build(context, location, input)
            .fold(
                ifFailure = { failure ->
                    if (failFast) return failure
                    failures.add(failure)
                    failures.merge()
                },
                ifSuccess = { success ->
                    if (failures.isNotEmpty()) failures.merge() else success
                }
            )
    }
}
