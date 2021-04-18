package io.github.airflux.dsl.reader.`object`

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsObject
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.asFailure
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

class ObjectReader(
    private val pathMissingErrorBuilder: () -> JsError,
    private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> JsError
) {

    operator fun <T> invoke(
        configuration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
        validations: ObjectValidations.Builder = ObjectValidations.Builder.Default,
        init: ObjectReader.Builder<T>.() -> Unit
    ): JsReader<T> = Builder<T>(configuration, validations).apply(init).build()

    @ObjectReaderMarker
    inner class Builder<R>(
        initialConfiguration: ObjectReaderConfiguration,
        initialValidation: ObjectValidations.Builder
    ) {

        private var configuration: ObjectReaderConfiguration = initialConfiguration
        private val validations: ObjectValidations.Builder = ObjectValidations.Builder(initialValidation)
        private val attributes = mutableListOf<Attribute<*>>()
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

        fun <T : Any> attribute(name: String, reader: JsReader<T>): AttributeBinder<T> =
            AttributeBinder(Attribute.Name.of(JsPath.empty / name), reader)

        fun <T : Any> attribute(path: JsPath, reader: JsReader<T>): AttributeBinder<T> =
            AttributeBinder(Attribute.Name.of(path), reader)

        fun build(): JsReader<R> {
            val validations = validations.build(configuration, attributes)
            val typeBuilder = typeBuilder ?: throw IllegalStateException("Builder for type is undefined.")
            return JsReader { input ->
                input.readAsObject(invalidTypeErrorBuilder) {
                    read(configuration, validations, attributes, typeBuilder, it)
                }
            }
        }

        private fun <T : Any> registration(attribute: Attribute<T>) {
            attributes.add(attribute)
        }

        @Suppress("unused")
        inner class AttributeBinder<T : Any>(
            private val name: Attribute.Name,
            private val reader: JsReader<T>,
        ) {

            fun required(): Attribute.Required<T> =
                Attribute.Required(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun defaultable(default: () -> T): Attribute.Defaultable<T> =
                Attribute.Defaultable(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(): Attribute.Optional<T> =
                Attribute.Optional(name, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(default: () -> T): Attribute.OptionalWithDefault<T> =
                Attribute.OptionalWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(): Attribute.Nullable<T> =
                Attribute.Nullable(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(default: () -> T): Attribute.NullableWithDefault<T> =
                Attribute.NullableWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }
        }
    }

    companion object {

        internal fun <R> read(
            configuration: ObjectReaderConfiguration,
            validation: ObjectValidations,
            attributes: List<Attribute<*>>,
            typeBuilder: (ObjectValuesMap) -> JsResult<R>,
            input: JsObject
        ): JsResult<R> {
            val preValidationErrors = preValidation(configuration, input, validation, attributes)
            if (preValidationErrors.isNotEmpty())
                return preValidationErrors.asFailure()

            val parseErrors = mutableListOf<JsResult.Failure>()
            val objectValuesMap = ObjectValuesMap.Builder()
                .apply {
                    attributes.forEach { attr: Attribute<*> ->
                        readValue(attr, input)
                            ?.also { parseErrors.add(it) }
                        if (configuration.failFast && parseErrors.isNotEmpty()) return@apply
                    }
                }
                .build()

            if (parseErrors.isEmpty()) {
                val postValidationErrors = postValidation(configuration, input, validation, attributes, objectValuesMap)
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
            attributes: List<Attribute<*>>
        ): List<JsError> = mutableListOf<JsError>()
            .apply {
                validation.before
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, attributes)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }

        internal fun postValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validation: ObjectValidations,
            attributes: List<Attribute<*>>,
            objectValuesMap: ObjectValuesMap
        ): List<JsError> = mutableListOf<JsError>()
            .apply {
                validation.after
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, attributes, objectValuesMap)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }
    }
}
