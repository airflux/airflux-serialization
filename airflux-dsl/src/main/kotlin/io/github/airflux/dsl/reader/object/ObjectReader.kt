package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.extension.readAsObject
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.asFailure
import io.github.airflux.value.JsObject

class ObjectReader(
    private val initialConfiguration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
    private val initialValidatorBuilders: ObjectValidators.Builder = ObjectValidators.Builder.Default,
    private val pathMissingErrorBuilder: PathMissingErrorBuilder,
    private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
) {

    operator fun <T> invoke(init: Builder<T>.() -> Unit): JsReader<T> = Builder<T>().apply(init).build()

    @AirfluxMarker
    inner class Builder<T> {

        private var configuration: ObjectReaderConfiguration = initialConfiguration
        private val validatorBuilders: ObjectValidators.Builder = ObjectValidators.Builder(initialValidatorBuilders)
        private val properties = mutableListOf<JsProperty<*>>()

        var typeBuilder: ((ObjectValuesMap) -> JsResult<T>)? = null
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
            PropertyBinder(JsProperty.Name.of(JsPath.empty / name), reader)

        fun <P : Any> property(path: JsPath, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinder(JsProperty.Name.of(path), reader)

        internal fun build(): JsReader<T> {
            val validators = validatorBuilders.build(configuration, properties)
            val typeBuilder = typeBuilder ?: throw IllegalStateException("Builder for type is undefined.")
            return JsReader { input ->
                input.readAsObject(invalidTypeErrorBuilder) {
                    read(configuration, validators, properties, typeBuilder, it)
                }
            }
        }

        private fun <P : Any> registration(property: JsProperty<P>) {
            properties.add(property)
        }

        @Suppress("unused")
        inner class PropertyBinder<P : Any>(private val name: JsProperty.Name, private val reader: JsReader<P>) {

            fun required(): JsProperty.Required<P> =
                JsProperty.Required(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun defaultable(default: () -> P): JsProperty.Defaultable<P> =
                JsProperty.Defaultable(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(): JsProperty.Optional<P> =
                JsProperty.Optional(name, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(default: () -> P): JsProperty.OptionalWithDefault<P> =
                JsProperty.OptionalWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(): JsProperty.Nullable<P> =
                JsProperty.Nullable(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(default: () -> P): JsProperty.NullableWithDefault<P> =
                JsProperty.NullableWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }
        }
    }

    companion object {

        internal fun <T> read(
            configuration: ObjectReaderConfiguration,
            validation: ObjectValidators,
            properties: List<JsProperty<*>>,
            typeBuilder: (ObjectValuesMap) -> JsResult<T>,
            input: JsObject
        ): JsResult<T> {
            val preValidationErrors = preValidation(configuration, input, validation, properties)
            if (preValidationErrors.isNotEmpty())
                return preValidationErrors.asFailure()

            val parseErrors = mutableListOf<JsResult.Failure>()
            val objectValuesMap = ObjectValuesMap.Builder()
                .apply {
                    properties.forEach { property ->
                        readValue(property, input)
                            ?.also { parseErrors.add(it) }
                        if (configuration.failFast && parseErrors.isNotEmpty()) return@apply
                    }
                }
                .build()

            if (parseErrors.isEmpty()) {
                val postValidationErrors = postValidation(configuration, input, validation, properties, objectValuesMap)
                if (postValidationErrors.isNotEmpty())
                    return postValidationErrors.asFailure()
            }

            return if (parseErrors.isEmpty())
                typeBuilder(objectValuesMap)
            else
                JsResult.Failure(parseErrors.fold(mutableListOf()) { acc, failure -> acc.apply { addAll(failure.errors) } })
        }

        internal fun preValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validation: ObjectValidators,
            properties: List<JsProperty<*>>
        ): List<JsError> = mutableListOf<JsError>()
            .apply {
                validation.before
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, properties)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }

        internal fun postValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validation: ObjectValidators,
            properties: List<JsProperty<*>>,
            objectValuesMap: ObjectValuesMap
        ): List<JsError> = mutableListOf<JsError>()
            .apply {
                validation.after
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, properties, objectValuesMap)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }
    }
}
