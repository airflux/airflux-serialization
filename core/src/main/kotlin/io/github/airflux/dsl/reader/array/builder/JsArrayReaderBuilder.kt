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

package io.github.airflux.dsl.reader.array.builder

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.JsArrayReader
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.array.readArray
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.readAsArray
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.array.builder.JsArrayReaderBuilder.ResultBuilder
import io.github.airflux.dsl.reader.array.builder.item.specification.JsArrayItemSpec
import io.github.airflux.dsl.reader.array.builder.item.specification.JsArrayPrefixItemsSpec
import io.github.airflux.dsl.reader.array.builder.validator.JsArrayValidatorBuilder
import io.github.airflux.dsl.reader.config.JsArrayReaderConfig
import io.github.airflux.dsl.reader.validator.JsArrayValidator

@AirfluxMarker
public class JsArrayReaderBuilder<T> internal constructor(configuration: JsArrayReaderConfig) {

    public fun interface ResultBuilder<T> : (JsReaderContext, JsLocation, JsArray<*>) -> JsResult<List<T>>

    private val validation: Validation.Builder<T> = configuration.validation
        .let { Validation.Builder(before = it.before) }

    public fun validation(block: Validation.Builder<T>.() -> Unit) {
        validation.block()
    }

    public fun returns(items: JsArrayItemSpec<T>): ResultBuilder<T> =
        ResultBuilder { context, location, input ->
            readArray(context = context, location = location, from = input, items = items.reader)
        }

    public fun returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: Boolean): ResultBuilder<T> =
        ResultBuilder { context, location, input ->
            readArray(
                context = context,
                location = location,
                from = input,
                prefixItems = prefixItems.readers(),
                errorIfAdditionalItems = !items
            )
        }

    public fun returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: JsArrayItemSpec<T>): ResultBuilder<T> =
        ResultBuilder { context, location, input ->
            readArray(
                context = context,
                location = location,
                from = input,
                prefixItems = prefixItems.readers(),
                items = items.reader
            )
        }

    internal fun build(resultBuilder: ResultBuilder<T>): JsArrayReader<T> {
        val configuration = buildConfiguration(resultBuilder)
        return JsArrayReader { context, location, input ->
            input.readAsArray(context, location) { c, l, v ->
                v.read(c, l, configuration)
            }
        }
    }

    private fun buildConfiguration(resultBuilder: ResultBuilder<T>): Configuration<T> {
        val validators = validation.build()
            .let {
                Configuration.Validators(
                    before = it.before?.build(),
                    after = it.after?.build()
                )
            }
        return Configuration(
            validators = validators,
            resultBuilder = resultBuilder
        )
    }

    public class Validation<T> private constructor(
        public val before: JsArrayValidatorBuilder.Before?,
        public val after: JsArrayValidatorBuilder.After<T>?
    ) {

        @AirfluxMarker
        public class Builder<T> internal constructor(
            public var before: JsArrayValidatorBuilder.Before? = null,
            public var after: JsArrayValidatorBuilder.After<T>? = null
        ) {
            internal fun build(): Validation<T> = Validation(before, after)
        }
    }

    internal data class Configuration<T>(
        val validators: Validators<T>,
        val resultBuilder: ResultBuilder<T>
    ) {
        internal data class Validators<T>(
            val before: JsArrayValidator.Before?,
            val after: JsArrayValidator.After<T>?
        )
    }

    internal companion object {

        internal fun <T> JsArrayPrefixItemsSpec<T>.readers(): List<JsReader<T>> =
            this.items.map { spec -> spec.reader }

        internal fun <T> JsArray<*>.read(
            context: JsReaderContext,
            location: JsLocation,
            configuration: Configuration<T>
        ): JsResult<List<T>> {

            val failures = mutableListOf<JsResult.Failure>()

            val preValidationFailure = configuration.validators.before
                ?.validation(context, location, this)
            if (preValidationFailure != null) {
                if (context.failFast) return preValidationFailure
                failures.add(preValidationFailure)
            }

            return configuration.resultBuilder(context, location, this)
                .fold(
                    ifFailure = { failure ->
                        if (context.failFast) return failure
                        failures.add(failure)
                        failures.merge()
                    },
                    ifSuccess = { success ->
                        val postValidationFailure = configuration.validators.after
                            ?.validation(context, location, this, success.value)
                        if (postValidationFailure != null) {
                            if (context.failFast) return postValidationFailure
                            failures.add(postValidationFailure)
                        }

                        if (failures.isNotEmpty()) failures.merge() else success
                    }
                )
        }
    }
}
