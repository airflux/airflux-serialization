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

package io.github.airflux.serialization.dsl.reader.array

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
import io.github.airflux.serialization.dsl.reader.array.item.specification.ArrayItemSpec
import io.github.airflux.serialization.dsl.reader.array.item.specification.ArrayPrefixItemsSpec
import io.github.airflux.serialization.dsl.reader.array.validator.ArrayValidator
import io.github.airflux.serialization.dsl.reader.array.validator.ArrayValidatorBuilder

public fun <EB, O, CTX, T> arrayReader(
    block: ArrayReader.Builder<EB, O, CTX, T>.() -> Reader<EB, O, CTX, List<T>>
): Reader<EB, O, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {
    val readerBuilder = ArrayReader.Builder<EB, O, CTX, T>()
    return block(readerBuilder)
}

public fun <EB, O, CTX, T> ArrayReader.Builder<EB, O, CTX, T>.returns(
    items: ArrayItemSpec<EB, O, CTX, T>
): Reader<EB, O, CTX, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(items)

public fun <EB, O, CTX, T> ArrayReader.Builder<EB, O, CTX, T>.returns(
    prefixItems: ArrayPrefixItemsSpec<EB, O, CTX, T>,
    items: Boolean
): Reader<EB, O, CTX, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(prefixItems, items)

public fun <EB, O, CTX, T> ArrayReader.Builder<EB, O, CTX, T>.returns(
    prefixItems: ArrayPrefixItemsSpec<EB, O, CTX, T>,
    items: ArrayItemSpec<EB, O, CTX, T>
): Reader<EB, O, CTX, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(prefixItems, items)

public class ArrayReader<EB, O, CTX, T> private constructor(
    private val validators: List<ArrayValidator<EB, O, CTX>>,
    private val readerResultBuilder: (ReaderEnv<EB, O>, CTX, Location, ArrayNode<*>) -> ReaderResult<List<T>>
) : Reader<EB, O, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    override fun read(
        env: ReaderEnv<EB, O>,
        context: CTX,
        location: Location,
        source: ValueNode
    ): ReaderResult<List<T>> =
        if (source is ArrayNode<*>)
            read(env, context, location, source)
        else
            ReaderResult.Failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(ArrayNode.nameOfType), source.nameOfType)
            )

    private fun read(
        env: ReaderEnv<EB, O>,
        context: CTX,
        location: Location,
        source: ArrayNode<*>
    ): ReaderResult<List<T>> {
        val failFast = env.options.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(env, context, location, source)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        return readerResultBuilder(env, context, location, source)
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

    @AirfluxMarker
    public class Builder<EB, O, CTX, T>
        where EB : AdditionalItemsErrorBuilder,
              EB : InvalidTypeErrorBuilder,
              O : FailFastOption {

        private val validatorBuilders = mutableListOf<ArrayValidatorBuilder<EB, O, CTX>>()

        public fun validation(
            validator: ArrayValidatorBuilder<EB, O, CTX>,
            vararg validators: ArrayValidatorBuilder<EB, O, CTX>
        ) {
            validation(
                validators = mutableListOf<ArrayValidatorBuilder<EB, O, CTX>>().apply {
                    add(validator)
                    addAll(validators)
                }
            )
        }

        public fun validation(validators: List<ArrayValidatorBuilder<EB, O, CTX>>) {
            validatorBuilders.addAll(validators)
        }

        public fun build(items: ArrayItemSpec<EB, O, CTX, T>): Reader<EB, O, CTX, List<T>> =
            build { env, context, location, source ->
                readArray(env = env, context = context, location = location, source = source, items = items.reader)
            }

        public fun build(
            prefixItems: ArrayPrefixItemsSpec<EB, O, CTX, T>,
            items: Boolean
        ): Reader<EB, O, CTX, List<T>> {
            val prefixItemReaders = prefixItems.readers
            return build { env, context, location, source ->
                readArray(
                    env = env,
                    context = context,
                    location = location,
                    source = source,
                    prefixItems = prefixItemReaders,
                    errorIfAdditionalItems = !items
                )
            }
        }

        public fun build(
            prefixItems: ArrayPrefixItemsSpec<EB, O, CTX, T>,
            items: ArrayItemSpec<EB, O, CTX, T>
        ): Reader<EB, O, CTX, List<T>> {
            val prefixItemReaders = prefixItems.readers
            return build { env, context, location, source ->
                readArray(
                    env = env,
                    context = context,
                    location = location,
                    source = source,
                    prefixItems = prefixItemReaders,
                    items = items.reader
                )
            }
        }

        private fun build(
            block: (ReaderEnv<EB, O>, CTX, Location, ArrayNode<*>) -> ReaderResult<List<T>>
        ): Reader<EB, O, CTX, List<T>> {
            val validators = validatorBuilders.map { builder -> builder.build() }
                .takeIf { it.isNotEmpty() }
                .orEmpty()
            return ArrayReader(validators, block)
        }
    }
}
