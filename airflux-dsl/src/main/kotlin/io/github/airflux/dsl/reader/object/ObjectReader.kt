package io.github.airflux.dsl.reader.`object`

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsObject
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.asFailure
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

class ObjectReader<E : JsError>(
    private val pathMissingErrorBuilder: () -> E,
    private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
) {

    operator fun <T> invoke(
        configuration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
        validations: ObjectValidations.Builder<E> = ObjectValidations.Builder(),
        init: Builder<T, E>.() -> Unit
    ): JsReader<T, E> =
        Builder<T, E>(configuration, validations, pathMissingErrorBuilder, invalidTypeErrorBuilder).apply(init).build()

    @ObjectReaderMarker
    class Builder<R, E : JsError>(
        initialConfiguration: ObjectReaderConfiguration,
        initialValidation: ObjectValidations.Builder<E>,
        private val pathMissingErrorBuilder: () -> E,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) {

        private var configuration: ObjectReaderConfiguration = initialConfiguration
        private val validations: ObjectValidations.Builder<E> = ObjectValidations.Builder(initialValidation)
        private val attributes = mutableListOf<Attribute<*, E>>()
        var typeBuilder: ((ObjectValuesMap<E>) -> JsResult<R, E>)? = null
            set(value) {
                if (field == null) field = value else throw IllegalStateException("Reassigned type builder.")
            }

        fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit) {
            configuration = ObjectReaderConfiguration.Builder(configuration).apply(init).build()
        }

        fun validation(init: ObjectValidations.Builder<E>.() -> Unit) {
            validations.apply(init)
        }

        fun <T : Any> attribute(name: String, reader: JsReader<T, E>): AttributeBinder<T, E> =
            AttributeBinder(
                Attribute.Name.of(JsPath.empty / name),
                reader,
                ::registration,
                pathMissingErrorBuilder,
                invalidTypeErrorBuilder
            )

        fun <T : Any> attribute(path: JsPath, reader: JsReader<T, E>): AttributeBinder<T, E> =
            AttributeBinder(
                Attribute.Name.of(path),
                reader,
                ::registration,
                pathMissingErrorBuilder,
                invalidTypeErrorBuilder
            )

        fun build(): JsReader<R, E> {
            val validations = validations.build(configuration, attributes)
            val typeBuilder = typeBuilder ?: throw IllegalStateException("Builder for type is undefined.")
            return JsReader { input ->
                input.readAsObject(invalidTypeErrorBuilder) {
                    read(configuration, validations, attributes, typeBuilder, it)
                }
            }
        }

        private fun <T : Any> registration(attribute: Attribute<T, E>) {
            attributes.add(attribute)
        }

        @Suppress("unused")
        class AttributeBinder<T : Any, E : JsError>(
            private val name: Attribute.Name,
            private val reader: JsReader<T, E>,
            private val registration: (Attribute<T, E>) -> Unit,
            private val pathMissingErrorBuilder: () -> E,
            private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
        ) {

            fun required(): Attribute.Required<T, E> =
                Attribute.Required(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun defaultable(default: () -> T): Attribute.Defaultable<T, E> =
                Attribute.Defaultable(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(): Attribute.Optional<T, E> =
                Attribute.Optional(name, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun optional(default: () -> T): Attribute.OptionalWithDefault<T, E> =
                Attribute.OptionalWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(): Attribute.Nullable<T, E> =
                Attribute.Nullable(name, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun nullable(default: () -> T): Attribute.NullableWithDefault<T, E> =
                Attribute.NullableWithDefault(name, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }
        }
    }

    companion object {

        internal fun <R, E : JsError> read(
            configuration: ObjectReaderConfiguration,
            validation: ObjectValidations<E>,
            attributes: List<Attribute<*, E>>,
            typeBuilder: (ObjectValuesMap<E>) -> JsResult<R, E>,
            input: JsObject
        ): JsResult<R, E> {
            val preValidationErrors = preValidation(configuration, input, validation, attributes)
            if (preValidationErrors.isNotEmpty())
                return preValidationErrors.asFailure()

            val parseErrors = mutableListOf<JsResult.Failure<E>>()
            val objectValuesMap = ObjectValuesMap.Builder<E>()
                .apply {
                    attributes.forEach { attr: Attribute<*, E> ->
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

        internal fun <E : JsError> preValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validation: ObjectValidations<E>,
            attributes: List<Attribute<*, E>>
        ): List<E> = mutableListOf<E>()
            .apply {
                validation.before
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, attributes)
                        addAll(validationResult)
                        if (isNotEmpty() && configuration.failFast) return@forEach
                    }
            }

        internal fun <E : JsError> postValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validation: ObjectValidations<E>,
            attributes: List<Attribute<*, E>>,
            objectValuesMap: ObjectValuesMap<E>
        ): List<E> = mutableListOf<E>()
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
