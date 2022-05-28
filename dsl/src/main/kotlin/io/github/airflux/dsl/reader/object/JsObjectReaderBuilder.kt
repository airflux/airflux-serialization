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
import io.github.airflux.core.reader.context.exception.ExceptionsHandler
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.reader.result.failure
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.extension.readAsObject
import io.github.airflux.dsl.reader.`object`.property.JsDefaultableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperties
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsRequiredReaderProperty
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.scope.JsObjectReaderConfiguration

internal class JsObjectReaderBuilder<T>(configuration: JsObjectReaderConfiguration) : JsObjectReader.Builder<T> {
    private val validation: JsObjectValidation.Builder = configuration.validation
        .let { JsObjectValidation.Builder(before = it.before, after = it.after) }

    private val propertiesBuilder = JsReaderProperties.Builder()

    override var checkUniquePropertyPath: Boolean = configuration.checkUniquePropertyPath

    override fun validation(block: JsObjectValidation.Builder.() -> Unit) {
        validation.block()
    }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Required<P>): JsReaderProperty.Required<P> =
        JsRequiredReaderProperty(builder.build())
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Defaultable<P>): JsReaderProperty.Defaultable<P> =
        JsDefaultableReaderProperty(builder.build())
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Optional<P>): JsReaderProperty.Optional<P> =
        JsOptionalReaderProperty(builder.build())
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.OptionalWithDefault<P>): JsReaderProperty.OptionalWithDefault<P> =
        JsOptionalWithDefaultReaderProperty(builder.build())
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.Nullable<P>): JsReaderProperty.Nullable<P> =
        JsNullableReaderProperty(builder.build())
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(builder: JsReaderPropertySpecBuilder.NullableWithDefault<P>): JsReaderProperty.NullableWithDefault<P> =
        JsNullableWithDefaultReaderProperty(builder.build())
            .also { propertiesBuilder.add(it) }

    override fun returns(builder: ObjectValuesMap.(JsReaderContext) -> JsResult<T>): JsObjectReader.TypeBuilder<T> =
        JsObjectReader.TypeBuilder { context, values ->
            try {
                values.builder(context)
            } catch (expected: Throwable) {
                context.getOrNull(ExceptionsHandler)
                    ?.handleException(context, values.location, expected)
                    ?.failure(values.location)
                    ?: throw expected
            }
        }

    fun build(typeBuilder: JsObjectReader.TypeBuilder<T>): JsObjectReader<T> {
        val properties = propertiesBuilder.build(checkUniquePropertyPath)
        val validators = validation.build().let {
            JsObjectValidators(
                before = it.before.build(properties),
                after = it.after.build(properties)
            )
        }
        return JsObjectReader { context, location, input ->
            input.readAsObject(context, location) { c, l, v ->
                read(c, l, v, validators, properties, typeBuilder)
            }
        }
    }

    internal data class JsObjectValidators(
        val before: JsObjectValidator.Before,
        val after: JsObjectValidator.After
    )

    companion object {

        internal fun <T> read(
            context: JsReaderContext,
            location: JsLocation,
            input: JsObject,
            validators: JsObjectValidators,
            properties: JsReaderProperties,
            typeBuilder: JsObjectReader.TypeBuilder<T>
        ): JsResult<T> {
            val failFast = context.failFast
            val failures = mutableListOf<JsResult.Failure>()

            val preValidationErrors = validators.before.validation(context, properties, input)
            if (preValidationErrors != null) {
                val failure = JsResult.Failure(location, preValidationErrors)
                if (failFast) return failure
                failures.add(failure)
            }

            val objectValuesMap = ObjectValuesMap.builder(context, location, input)
                .apply {
                    properties.forEach { property ->
                        val failure = tryAddValueBy(property)
                        if (failure != null) {
                            if (failFast) return failure
                            failures.add(failure)
                        }
                    }
                }
                .build()

            val postValidationErrors = validators.after.validation(context, properties, objectValuesMap, input)
            if (postValidationErrors != null) {
                val failure = JsResult.Failure(location, postValidationErrors)
                if (failFast) return failure
                failures.add(failure)
            }

            return if (failures.isEmpty())
                typeBuilder(context, objectValuesMap)
            else
                failures.merge()
        }
    }
}
