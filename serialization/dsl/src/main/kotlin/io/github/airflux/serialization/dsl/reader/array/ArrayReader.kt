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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.readArray
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.ifInvalid
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.AirfluxMarker
import io.github.airflux.serialization.dsl.reader.array.validation.ArrayValidator

public fun <EB, O, T> arrayReader(
    block: ArrayReader.Builder<EB, O, T>.() -> JsReader<EB, O, List<T>>
): JsReader<EB, O, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {
    val readerBuilder = ArrayReader.Builder<EB, O, T>()
    return block(readerBuilder)
}

public fun <EB, O, T> ArrayReader.Builder<EB, O, T>.returns(
    items: JsReader<EB, O, T>
): JsReader<EB, O, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(items)

public fun <EB, O, T> ArrayReader.Builder<EB, O, T>.returns(
    prefixItems: ArrayPrefixItems<EB, O, T>,
    items: Boolean
): JsReader<EB, O, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(prefixItems, items)

public fun <EB, O, T> ArrayReader.Builder<EB, O, T>.returns(
    prefixItems: ArrayPrefixItems<EB, O, T>,
    items: JsReader<EB, O, T>
): JsReader<EB, O, List<T>>
    where EB : InvalidTypeErrorBuilder,
          EB : AdditionalItemsErrorBuilder,
          O : FailFastOption = this.build(prefixItems, items)

public class ArrayReader<EB, O, T> private constructor(
    private val validators: List<ArrayValidator<EB, O>>,
    private val resultBuilder: (JsReaderEnv<EB, O>, JsLocation, JsArray) -> JsReaderResult<List<T>>
) : JsReader<EB, O, List<T>>
    where EB : AdditionalItemsErrorBuilder,
          EB : InvalidTypeErrorBuilder,
          O : FailFastOption {

    override fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsValue): JsReaderResult<List<T>> =
        if (source is JsArray)
            read(env, location, source)
        else
            failure(
                location = location,
                error = env.errorBuilders.invalidTypeError(listOf(JsArray.nameOfType), source.nameOfType)
            )

    private fun read(env: JsReaderEnv<EB, O>, location: JsLocation, source: JsArray): JsReaderResult<List<T>> {
        val failFast = env.options.failFast
        val failureAccumulator: JsReaderResult.Failure? = source.validate(env, location)
        if (failureAccumulator != null && failFast) return failureAccumulator

        return resultBuilder(env, location, source)
            .fold(
                onFailure = { failure -> failureAccumulator + failure },
                onSuccess = { success -> failureAccumulator ?: success }
            )
    }

    @AirfluxMarker
    public class Builder<EB, O, T> internal constructor()
        where EB : AdditionalItemsErrorBuilder,
              EB : InvalidTypeErrorBuilder,
              O : FailFastOption {

        private val validatorBuilders = mutableListOf<ArrayValidator.Builder<EB, O>>()

        public fun validation(
            validator: ArrayValidator.Builder<EB, O>,
            vararg validators: ArrayValidator.Builder<EB, O>
        ) {
            validation(
                validators = mutableListOf<ArrayValidator.Builder<EB, O>>()
                    .apply {
                        add(validator)
                        addAll(validators)
                    }
            )
        }

        public fun validation(validators: List<ArrayValidator.Builder<EB, O>>) {
            validatorBuilders.addAll(validators)
        }

        internal fun build(items: JsReader<EB, O, T>): JsReader<EB, O, List<T>> =
            build { env, location, source ->
                readArray(env = env, location = location, source = source, itemsReader = items)
            }

        internal fun build(
            prefixItems: ArrayPrefixItems<EB, O, T>,
            items: Boolean
        ): JsReader<EB, O, List<T>> =
            build { env, location, source ->
                readArray(
                    env = env,
                    location = location,
                    source = source,
                    prefixItemReaders = prefixItems,
                    errorIfAdditionalItems = !items
                )
            }

        internal fun build(
            prefixItems: ArrayPrefixItems<EB, O, T>,
            items: JsReader<EB, O, T>
        ): JsReader<EB, O, List<T>> =
            build { env, location, source ->
                readArray(
                    env = env,
                    location = location,
                    source = source,
                    prefixItemReaders = prefixItems,
                    itemsReader = items
                )
            }

        private fun build(
            block: (JsReaderEnv<EB, O>, JsLocation, JsArray) -> JsReaderResult<List<T>>
        ): JsReader<EB, O, List<T>> {
            val validators = validatorBuilders.map { builder -> builder.build() }
                .takeIf { it.isNotEmpty() }
                .orEmpty()
            return ArrayReader(validators, block)
        }
    }

    private fun JsArray.validate(env: JsReaderEnv<EB, O>, location: JsLocation): JsReaderResult.Failure? {
        val failFast = env.options.failFast
        var failureAccumulator: JsReaderResult.Failure? = null

        validators.forEach { validator ->
            validator.validate(env, location, this)
                .ifInvalid { failure ->
                    if (failFast) return failure
                    failureAccumulator += failure
                }
        }
        return failureAccumulator
    }
}
