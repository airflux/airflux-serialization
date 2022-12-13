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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.array.readArray
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.array.builder.ArrayReaderBuilder.ResultBuilder
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.ArrayItemSpec
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.ArrayPrefixItemsSpec
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayReaderValidation
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayReaderValidationInstance
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidator

public fun <EB, CTX, T> arrayReader(
    block: ArrayReaderBuilder<EB, CTX, T>.() -> ResultBuilder<EB, CTX, T>
): Reader<EB, CTX, T>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {
    val readerBuilder: ArrayReaderBuilder<EB, CTX, T> =
        ArrayReaderBuilder(ArrayReaderValidationInstance())
    val resultBuilder: ResultBuilder<EB, CTX, T> = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

@AirfluxMarker
public class ArrayReaderBuilder<EB, CTX, T> internal constructor(
    private val validatorsBuilder: ArrayReaderValidationInstance<EB, CTX>
) : ArrayReaderValidation<EB, CTX> by validatorsBuilder
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {

    public fun interface ResultBuilder<EB, CTX, T>
        where EB : AdditionalItemsErrorBuilder,
              CTX : FailFastOption {
        public fun build(env: ReaderEnv<EB, CTX>, location: Location, source: ArrayNode<*>): ReaderResult<T>
    }

    internal fun build(resultBuilder: ResultBuilder<EB, CTX, T>): Reader<EB, CTX, T> {
        val validators = validatorsBuilder.build()
        return ArrayReader(validators, resultBuilder)
    }
}

public fun <EB, CTX, T> returns(items: ArrayItemSpec<EB, CTX, T>): ResultBuilder<EB, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          CTX : FailFastOption =
    ResultBuilder { env, location, source ->
        readArray(env = env, location = location, source = source, items = items.reader)
    }

public fun <EB, CTX, T> returns(
    prefixItems: ArrayPrefixItemsSpec<EB, CTX, T>,
    items: Boolean
): ResultBuilder<EB, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          CTX : FailFastOption {
    val prefixItemReaders = prefixItems.readers
    return ResultBuilder { env, location, source ->
        readArray(
            env = env,
            location = location,
            source = source,
            prefixItems = prefixItemReaders,
            errorIfAdditionalItems = !items
        )
    }
}

public fun <EB, CTX, T> returns(
    prefixItems: ArrayPrefixItemsSpec<EB, CTX, T>,
    items: ArrayItemSpec<EB, CTX, T>
): ResultBuilder<EB, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          CTX : FailFastOption {
    val prefixItemReaders = prefixItems.readers
    return ResultBuilder { env, location, source ->
        readArray(
            env = env,
            location = location,
            source = source,
            prefixItems = prefixItemReaders,
            items = items.reader
        )
    }
}

internal class ArrayReader<EB, CTX, T>(
    private val validators: List<ArrayValidator<EB, CTX>>,
    private val resultBuilder: ResultBuilder<EB, CTX, T>
) : Reader<EB, CTX, T>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {

    override fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ValueNode): ReaderResult<T> =
        if (source is ArrayNode<*>)
            read(env, location, source)
        else
            ReaderResult.Failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(ArrayNode.nameOfType), source.nameOfType)
            )

    private fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ArrayNode<*>): ReaderResult<T> {
        val failFast = env.context.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(env, location, source)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        return resultBuilder.build(env, location, source)
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
