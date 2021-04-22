package io.github.airflux.dsl.reader.`object`

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
    private val initialValidations: ObjectValidations.Builder = ObjectValidations.Builder.Default,
    private val pathMissingErrorBuilder: PathMissingErrorBuilder,
    private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
) {

    operator fun <T> invoke(init: ObjectReader.Builder<T>.() -> Unit): JsReader<T> =
        Builder<T>().apply(init).build()

    @ObjectReaderMarker
    inner class Builder<R> {

        private var configuration: ObjectReaderConfiguration = initialConfiguration
        private val validations: ObjectValidations.Builder = ObjectValidations.Builder(initialValidations)
        private val properties = mutableListOf<JsProperty<*>>()
        var typeBuilder: ((ObjectValuesMap) -> JsResult<R>)? = null
            set(value) {
                if (field == null) field = value else throw IllegalStateException("Reassigned type builder.")
            }

        fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit) {
            configuration = ObjectReaderConfiguration.Builder(configuration).apply(init).build()
        }

        fun validation(init: ObjectValidations.Builder.() -> Unit) {
            validations.apply(init)
        }

        fun <T : Any> property(name: String, reader: JsReader<T>): PropertyBinder<T> =
            PropertyBinder(JsProperty.Name.of(JsPath.empty / name), reader)

        fun <T : Any> property(path: JsPath, reader: JsReader<T>): PropertyBinder<T> =
            PropertyBinder(JsProperty.Name.of(path), reader)

        fun build(): JsReader<R> {
            val validations = validations.build(configuration, properties)
            val typeBuilder = typeBuilder ?: throw IllegalStateException("Builder for type is undefined.")
            return JsReader { input ->
                input.readAsObject(invalidTypeErrorBuilder) {
                    read(configuration, validations, properties, typeBuilder, it)
                }
            }
        }

        private fun <T : Any> registration(property: JsProperty<T>) {
            properties.add(property)
        }

        @Suppress("unused")
        inner class PropertyBinder<T : Any>(
            private val name: JsProperty.Name,
            private val reader: JsReader<T>,
        ) {

            fun required(): JsProperty.Required<T> =
                JsProperty.Required(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun defaultable(default: () -> T): JsProperty.Defaultable<T> =
                JsProperty.Defaultable(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(): JsProperty.Optional<T> =
                JsProperty.Optional(name, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(default: () -> T): JsProperty.OptionalWithDefault<T> =
                JsProperty.OptionalWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(): JsProperty.Nullable<T> =
                JsProperty.Nullable(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(default: () -> T): JsProperty.NullableWithDefault<T> =
                JsProperty.NullableWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }
        }
    }

    companion object {

        internal fun <R> read(
            configuration: ObjectReaderConfiguration,
            validation: ObjectValidations,
            properties: List<JsProperty<*>>,
            typeBuilder: (ObjectValuesMap) -> JsResult<R>,
            input: JsObject
        ): JsResult<R> {
            val preValidationErrors = preValidation(configuration, input, validation, properties)
            if (preValidationErrors.isNotEmpty())
                return preValidationErrors.asFailure()

            val parseErrors = mutableListOf<JsResult.Failure>()
            val objectValuesMap = ObjectValuesMap.Builder()
                .apply {
                    properties.forEach { attr: JsProperty<*> ->
                        readValue(attr, input)
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
            validation: ObjectValidations,
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
            validation: ObjectValidations,
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
