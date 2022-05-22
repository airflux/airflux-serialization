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

package io.github.airflux.dsl.reader.`object`

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.extension.readAsObject
import io.github.airflux.dsl.reader.JsReaderBuilder
import io.github.airflux.dsl.reader.`object`.property.JsDefaultableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsRequiredReaderProperty
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidators

internal class JsObjectReaderBuilder<T>(
    private val config: JsReaderBuilder.Configuration
) : JsObjectReader.Builder<T> {
    private val validatorBuilders: JsObjectValidators.Builder = JsObjectValidators.Builder()
    private val properties = mutableListOf<JsReaderProperty>()

    override fun validation(init: JsObjectValidators.Builder.() -> Unit) {
        validatorBuilders.apply(init)
    }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Required<P>): JsReaderProperty.Required<P> =
        JsRequiredReaderProperty(builder.build())
            .also { registration(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Defaultable<P>): JsReaderProperty.Defaultable<P> =
        JsDefaultableReaderProperty(builder.build())
            .also { registration(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Optional<P>): JsReaderProperty.Optional<P> =
        JsOptionalReaderProperty(builder.build())
            .also { registration(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.OptionalWithDefault<P>): JsReaderProperty.OptionalWithDefault<P> =
        JsOptionalWithDefaultReaderProperty(builder.build())
            .also { registration(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Nullable<P>): JsReaderProperty.Nullable<P> =
        JsNullableReaderProperty(builder.build())
            .also { registration(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.NullableWithDefault<P>): JsReaderProperty.NullableWithDefault<P> =
        JsNullableWithDefaultReaderProperty(builder.build())
            .also { registration(it) }

    override fun build(builder: ObjectValuesMap.() -> JsResult<T>): JsObjectReader.TypeBuilder<T> =
        JsObjectReader.TypeBuilder { v ->
            try {
                v.builder()
            } catch (expected: Throwable) {
                config.exceptionHandlers[expected]
                    ?.invoke(v.location, expected)
                    ?: throw expected
            }
        }

    fun build(typeBuilder: JsObjectReader.TypeBuilder<T>): JsObjectReader<T> {
        val validators = validatorBuilders.build(config, properties)
        return JsObjectReader { context, location, input ->
            input.readAsObject(context, location) { c, l, v ->
                read(config, validators, properties, typeBuilder, c, l, v)
            }
        }
    }

    private fun registration(property: JsReaderProperty) {
        properties.add(property)
    }

    companion object {

        internal fun <T> read(
            options: JsReaderBuilder.Options,
            validators: JsObjectValidators,
            properties: List<JsReaderProperty>,
            typeBuilder: JsObjectReader.TypeBuilder<T>,
            context: JsReaderContext,
            location: JsLocation,
            input: JsObject
        ): JsResult<T> {
            val failures = mutableListOf<JsResult.Failure>()

            val preValidationErrors = validators.before
                ?.validation(context, properties, input)
            if (preValidationErrors != null) {
                val failure = JsResult.Failure(location, preValidationErrors)
                if (options.failFast) return failure
                failures.add(failure)
            }

            val objectValuesMap = ObjectValuesMap.builder(context, location, input)
                .apply {
                    properties.forEach { property ->
                        val failure = tryAddValueBy(property)
                        if (failure != null) {
                            if (options.failFast) return failure
                            failures.add(failure)
                        }
                    }
                }
                .build()

            val postValidationErrors = validators.after
                ?.validation(context, properties, objectValuesMap, input)
            if (postValidationErrors != null) {
                val failure = JsResult.Failure(location, postValidationErrors)
                if (options.failFast) return failure
                failures.add(failure)
            }

            return if (failures.isEmpty())
                typeBuilder(objectValuesMap)
            else
                failures.merge()
        }
    }
}
