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
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.ArrayItemSpec
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.ArrayPrefixItemsSpec
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidator
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidatorBuilder

public fun <EB, CTX, T> arrayReader(
    block: ArrayReader.Builder<EB, CTX, T>.() -> Reader<EB, CTX, List<T>>
): Reader<EB, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {
    val readerBuilder = ArrayReader.Builder<EB, CTX, T>()
    return block(readerBuilder)
}

public fun <EB, CTX, T> ArrayReader.Builder<EB, CTX, T>.returns(
    items: ArrayItemSpec<EB, CTX, T>
): Reader<EB, CTX, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          CTX : FailFastOption = this.build(items)

public fun <EB, CTX, T> ArrayReader.Builder<EB, CTX, T>.returns(
    prefixItems: ArrayPrefixItemsSpec<EB, CTX, T>,
    items: Boolean
): Reader<EB, CTX, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          CTX : FailFastOption = this.build(prefixItems, items)

public fun <EB, CTX, T> ArrayReader.Builder<EB, CTX, T>.returns(
    prefixItems: ArrayPrefixItemsSpec<EB, CTX, T>,
    items: ArrayItemSpec<EB, CTX, T>
): Reader<EB, CTX, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          CTX : FailFastOption = this.build(prefixItems, items)

public class ArrayReader<EB, CTX, T> private constructor(
    private val validators: List<ArrayValidator<EB, CTX>>,
    private val readerResultBuilder: (ReaderEnv<EB, CTX>, Location, ArrayNode<*>) -> ReaderResult<List<T>>
) : Reader<EB, CTX, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          CTX : FailFastOption {

    override fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ValueNode): ReaderResult<List<T>> =
        if (source is ArrayNode<*>)
            read(env, location, source)
        else
            ReaderResult.Failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(ArrayNode.nameOfType), source.nameOfType)
            )

    private fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ArrayNode<*>): ReaderResult<List<T>> {
        val failFast = env.context.failFast
        val failures = mutableListOf<ReaderResult.Failure>()

        validators.forEach { validator ->
            val failure = validator.validate(env, location, source)
            if (failure != null) {
                if (failFast) return failure
                failures.add(failure)
            }
        }

        return readerResultBuilder(env, location, source)
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
    public class Builder<EB, CTX, T>
        where EB : AdditionalItemsErrorBuilder,
              EB : InvalidTypeErrorBuilder,
              CTX : FailFastOption {

        private val validatorBuilders = mutableListOf<ArrayValidatorBuilder<EB, CTX>>()

        public fun validation(
            validator: ArrayValidatorBuilder<EB, CTX>,
            vararg validators: ArrayValidatorBuilder<EB, CTX>
        ) {
            validation(
                validators = mutableListOf<ArrayValidatorBuilder<EB, CTX>>().apply {
                    add(validator)
                    addAll(validators)
                }
            )
        }

        public fun validation(validators: List<ArrayValidatorBuilder<EB, CTX>>) {
            validatorBuilders.addAll(validators)
        }

        public fun build(items: ArrayItemSpec<EB, CTX, T>): Reader<EB, CTX, List<T>> =
            build { env, location, source ->
                readArray(env = env, location = location, source = source, items = items.reader)
            }

        public fun build(prefixItems: ArrayPrefixItemsSpec<EB, CTX, T>, items: Boolean): Reader<EB, CTX, List<T>> {
            val prefixItemReaders = prefixItems.readers
            return build { env, location, source ->
                readArray(
                    env = env,
                    location = location,
                    source = source,
                    prefixItems = prefixItemReaders,
                    errorIfAdditionalItems = !items
                )
            }
        }

        public fun build(
            prefixItems: ArrayPrefixItemsSpec<EB, CTX, T>,
            items: ArrayItemSpec<EB, CTX, T>
        ): Reader<EB, CTX, List<T>> {
            val prefixItemReaders = prefixItems.readers
            return build { env, location, source ->
                readArray(
                    env = env,
                    location = location,
                    source = source,
                    prefixItems = prefixItemReaders,
                    items = items.reader
                )
            }
        }

        private fun build(
            block: (ReaderEnv<EB, CTX>, Location, ArrayNode<*>) -> ReaderResult<List<T>>
        ): Reader<EB, CTX, List<T>> {
            val validators = validatorBuilders.map { builder -> builder.build() }
                .takeIf { it.isNotEmpty() }
                .orEmpty()
            return ArrayReader(validators, block)
        }
    }
}
