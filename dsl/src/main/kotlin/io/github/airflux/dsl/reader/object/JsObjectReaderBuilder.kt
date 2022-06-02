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

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.exception.ExceptionsHandler
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.reader.result.failure
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.extension.readAsObject
import io.github.airflux.dsl.reader.`object`.property.JsObjectReaderProperties
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.property.specification.JsObjectReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.scope.JsObjectReaderConfiguration

internal class JsObjectReaderBuilder<T>(configuration: JsObjectReaderConfiguration) : JsObjectReader.Builder<T> {
    private val validation: JsObjectReader.Validation.Builder = configuration.validation
        .let { JsObjectReader.Validation.Builder(before = it.before, after = it.after) }

    private val propertiesBuilder = JsObjectReaderProperties.Builder()

    override var checkUniquePropertyPath: Boolean = configuration.checkUniquePropertyPath

    override fun validation(block: JsObjectReader.Validation.Builder.() -> Unit) {
        validation.block()
    }

    override fun <P : Any> property(spec: JsObjectReaderPropertySpec.Required<P>): JsObjectProperty.Required<P> =
        JsObjectProperty.Required(spec)
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(spec: JsObjectReaderPropertySpec.Defaultable<P>): JsObjectProperty.Defaultable<P> =
        JsObjectProperty.Defaultable(spec)
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(spec: JsObjectReaderPropertySpec.Optional<P>): JsObjectProperty.Optional<P> =
        JsObjectProperty.Optional(spec)
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(spec: JsObjectReaderPropertySpec.OptionalWithDefault<P>): JsObjectProperty.OptionalWithDefault<P> =
        JsObjectProperty.OptionalWithDefault(spec)
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(spec: JsObjectReaderPropertySpec.Nullable<P>): JsObjectProperty.Nullable<P> =
        JsObjectProperty.Nullable(spec)
            .also { propertiesBuilder.add(it) }

    override fun <P : Any> property(spec: JsObjectReaderPropertySpec.NullableWithDefault<P>): JsObjectProperty.NullableWithDefault<P> =
        JsObjectProperty.NullableWithDefault(spec)
            .also { propertiesBuilder.add(it) }

    override fun returns(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): JsObjectReader.TypeBuilder<T> =
        JsObjectReader.TypeBuilder { context, location, values ->
            try {
                values.builder(context, location)
            } catch (expected: Throwable) {
                context.getOrNull(ExceptionsHandler)
                    ?.handleException(context, location, expected)
                    ?.failure(location)
                    ?: throw expected
            }
        }

    fun build(typeBuilder: JsObjectReader.TypeBuilder<T>): JsObjectReader<T> {
        val configuration = buildConfiguration(typeBuilder = typeBuilder)
        return JsObjectReader { context, location, input ->
            input.readAsObject(context, location) { c, l, i ->
                i.read(c, l, configuration)
            }
        }
    }

    private fun buildConfiguration(typeBuilder: JsObjectReader.TypeBuilder<T>): Configuration<T> {
        val properties: JsObjectReaderProperties = propertiesBuilder.build(checkUniquePropertyPath)
        val validators = validation.build()
            .let {
                Configuration.Validators(
                    before = it.before?.build(properties),
                    after = it.after?.build(properties)
                )
            }
        return Configuration(
            properties = properties,
            validators = validators,
            typeBuilder = typeBuilder
        )
    }

    internal data class Configuration<T>(
        val properties: JsObjectReaderProperties,
        val validators: Validators,
        val typeBuilder: JsObjectReader.TypeBuilder<T>
    ) {
        internal data class Validators(
            val before: JsObjectValidator.Before?,
            val after: JsObjectValidator.After?
        )
    }

    companion object {

        internal fun <T> JsObject.read(
            context: JsReaderContext,
            location: JsLocation,
            configuration: Configuration<T>
        ): JsResult<T> {
            val failFast = context.failFast
            val failures = mutableListOf<JsResult.Failure>()

            val preValidationErrors = configuration.validators.before
                ?.validation(context, configuration.properties, this)
            if (preValidationErrors != null) {
                val failure = JsResult.Failure(location, preValidationErrors)
                if (failFast) return failure
                failures.add(failure)
            }

            val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance()
                .apply {
                    configuration.properties.forEach { property ->
                        this@read.read(context, location, property)
                            .fold(
                                ifFailure = { failure ->
                                    if (failFast) return failure
                                    failures.add(failure)
                                },
                                ifSuccess = { value ->
                                    this[property] = value
                                }
                            )
                    }
                }

            val postValidationErrors = configuration.validators.after
                ?.validation(context, configuration.properties, objectValuesMap, this)
            if (postValidationErrors != null) {
                val failure = JsResult.Failure(location, postValidationErrors)
                if (failFast) return failure
                failures.add(failure)
            }

            return if (failures.isEmpty())
                configuration.typeBuilder(context, location, objectValuesMap)
            else
                failures.merge()
        }

        internal fun JsObject.read(
            context: JsReaderContext,
            location: JsLocation,
            property: JsObjectProperty
        ): JsResult<Any?> {
            fun JsObjectProperty.getReader(): JsReader<Any?> = when (this) {
                is JsObjectProperty.Required<*> -> this.reader
                is JsObjectProperty.Defaultable<*> -> this.reader
                is JsObjectProperty.Optional<*> -> this.reader
                is JsObjectProperty.OptionalWithDefault<*> -> this.reader
                is JsObjectProperty.Nullable<*> -> this.reader
                is JsObjectProperty.NullableWithDefault<*> -> this.reader
            }

            return property.getReader().read(context, location, this)
        }
    }
}
