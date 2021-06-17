package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderPropertyInstance
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidatorInstances
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidators
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.extension.readAsObject
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.result.asFailure
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

fun <T : Any> JsValue.deserialization(context: JsReaderContext? = null, reader: JsReader<T>): JsResult<T> =
    reader.read(context, JsResultPath.Root, this)

class ObjectReader(
    private val initialConfiguration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
    private val initialValidatorBuilders: ObjectValidators = ObjectValidators.Default,
    private val pathMissingErrorBuilder: PathMissingErrorBuilder,
    private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
) {

    operator fun <T> invoke(init: Builder<T>.() -> Unit): JsReader<T> = Builder<T>().apply(init).build()

    @AirfluxMarker
    inner class Builder<T> internal constructor() {

        private var configuration: ObjectReaderConfiguration = initialConfiguration
        private val validatorBuilders: ObjectValidators.Builder = ObjectValidators.Builder(initialValidatorBuilders)
        private val properties = mutableListOf<JsReaderProperty<*>>()

        var typeBuilder: ((ObjectValuesMap, JsResultPath) -> JsResult<T>)? = null
            set(value) {
                if (field == null) field = value else throw IllegalStateException("Reassigned type builder.")
            }

        fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit) {
            configuration = ObjectReaderConfiguration.Builder(configuration).apply(init).build()
        }

        fun validation(init: ObjectValidators.Builder.() -> Unit) {
            validatorBuilders.apply(init)
        }

        fun <P : Any> property(name: String, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinder(JsPath.Root / name, reader)

        fun <P : Any> property(path: JsPath.Identifiable, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinder(path, reader)

        internal fun build(): JsReader<T> {
            val validators = ObjectValidatorInstances.of(validatorBuilders.build(), configuration, properties)
            val typeBuilder = typeBuilder ?: throw IllegalStateException("Builder for type is undefined.")
            return JsReader { context, path, input ->
                input.readAsObject(path, invalidTypeErrorBuilder) { a, b ->
                    read(configuration, validators, properties, typeBuilder, context, a, b)
                }
            }
        }

        private fun <P : Any> registration(property: JsReaderProperty<P>) {
            properties.add(property)
        }

        @Suppress("unused")
        inner class PropertyBinder<P : Any> internal constructor(
            private val attributePath: JsPath.Identifiable,
            private val reader: JsReader<P>
        ) {

            fun required(): JsReaderProperty.Required<P> =
                JsReaderPropertyInstance.Required(
                    attributePath,
                    reader,
                    pathMissingErrorBuilder,
                    invalidTypeErrorBuilder
                )
                    .also { registration(it) }

            fun defaultable(default: () -> P): JsReaderProperty.Defaultable<P> =
                JsReaderPropertyInstance.Defaultable(attributePath, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(): JsReaderProperty.Optional<P> =
                JsReaderPropertyInstance.Optional(attributePath, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(default: () -> P): JsReaderProperty.OptionalWithDefault<P> =
                JsReaderPropertyInstance.OptionalWithDefault(attributePath, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(): JsReaderProperty.Nullable<P> =
                JsReaderPropertyInstance.Nullable(
                    attributePath,
                    reader,
                    pathMissingErrorBuilder,
                    invalidTypeErrorBuilder
                )
                    .also { registration(it) }

            fun nullable(default: () -> P): JsReaderProperty.NullableWithDefault<P> =
                JsReaderPropertyInstance.NullableWithDefault(attributePath, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }
        }
    }

    companion object {

        internal fun <T> read(
            configuration: ObjectReaderConfiguration,
            validators: ObjectValidatorInstances,
            properties: List<JsReaderProperty<*>>,
            typeBuilder: (ObjectValuesMap, JsResultPath) -> JsResult<T>,
            context: JsReaderContext?,
            currentPath: JsResultPath,
            input: JsObject
        ): JsResult<T> {
            val preValidationErrors = preValidation(configuration, input, validators, properties, context)
            if (preValidationErrors.isNotEmpty())
                return preValidationErrors.asFailure(currentPath)

            val parseErrors = mutableListOf<JsResult.Failure>()
            val objectValuesMap = ObjectValuesMap.Builder()
                .apply {
                    properties.forEach { property ->
                        readValue(context, currentPath, property, input)
                            ?.also { parseErrors.add(it) }
                        if (configuration.failFast && parseErrors.isNotEmpty()) return@apply
                    }
                }
                .build()

            if (parseErrors.isEmpty()) {
                val postValidationErrors =
                    postValidation(configuration, input, validators, properties, objectValuesMap, context)
                if (postValidationErrors.isNotEmpty())
                    return postValidationErrors.asFailure(currentPath)
            }

            return if (parseErrors.isEmpty())
                typeBuilder(objectValuesMap, currentPath)
            else
                JsResult.Failure(parseErrors.fold(mutableListOf()) { acc, failure -> acc.apply { addAll(failure.errors) } })
        }

        internal fun preValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validators: ObjectValidatorInstances,
            properties: List<JsReaderProperty<*>>,
            context: JsReaderContext?
        ): List<JsError> = mutableListOf<JsError>()
            .apply {
                validators.before
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, properties, context)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }

        internal fun postValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validators: ObjectValidatorInstances,
            properties: List<JsReaderProperty<*>>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext?
        ): List<JsError> = mutableListOf<JsError>()
            .apply {
                validators.after
                    .forEach { validator ->
                        val validationResult =
                            validator.validation(configuration, input, properties, objectValuesMap, context)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }
    }
}
