package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.property.DefaultableProperty
import io.github.airflux.dsl.reader.`object`.property.DefaultablePropertyInstance
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.NullableProperty
import io.github.airflux.dsl.reader.`object`.property.NullablePropertyInstance
import io.github.airflux.dsl.reader.`object`.property.NullableWithDefaultProperty
import io.github.airflux.dsl.reader.`object`.property.NullableWithDefaultPropertyInstance
import io.github.airflux.dsl.reader.`object`.property.OptionalProperty
import io.github.airflux.dsl.reader.`object`.property.OptionalPropertyInstance
import io.github.airflux.dsl.reader.`object`.property.OptionalWithDefaultProperty
import io.github.airflux.dsl.reader.`object`.property.OptionalWithDefaultPropertyInstance
import io.github.airflux.dsl.reader.`object`.property.RequiredProperty
import io.github.airflux.dsl.reader.`object`.property.RequiredPropertyInstance
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorInstances
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidators
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.result.asFailure
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.readAsObject

fun <T : Any> JsValue.deserialization(context: JsReaderContext? = null, reader: JsReader<T>): JsResult<T> =
    reader.read(context, JsResultPath.Root, this)

class ObjectReader(
    private val initialConfiguration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
    private val initialValidatorBuilders: JsObjectValidators = JsObjectValidators.Default,
    private val pathMissingErrorBuilder: PathMissingErrorBuilder,
    private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
) {

    operator fun <T> invoke(init: Builder<T>.() -> TypeBuilder<T>): JsReader<T> {
        val builder = BuilderInstance<T>()
        val typeBuilder = builder.init()
        return builder.build(typeBuilder)
    }

    @AirfluxMarker
    interface Builder<T> {
        fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit)
        fun validation(init: JsObjectValidators.Builder.() -> Unit)

        fun <P : Any> property(name: String, reader: JsReader<P>): PropertyBinder<P>
        fun <P : Any> property(path: JsPath.Identifiable, reader: JsReader<P>): PropertyBinder<P>

        fun build(builder: (ObjectValuesMap) -> T): TypeBuilder<T>
        fun build(builder: (ObjectValuesMap, JsResultPath) -> JsResult<T>): TypeBuilder<T>
    }

    interface PropertyBinder<P : Any> {
        fun required(): RequiredProperty<P>
        fun defaultable(default: () -> P): DefaultableProperty<P>

        fun optional(): OptionalProperty<P>
        fun optional(default: () -> P): OptionalWithDefaultProperty<P>

        fun nullable(): NullableProperty<P>
        fun nullable(default: () -> P): NullableWithDefaultProperty<P>
    }

    fun interface TypeBuilder<T> : (ObjectValuesMap, JsResultPath) -> JsResult<T>

    private inner class BuilderInstance<T> : Builder<T> {
        private var configuration: ObjectReaderConfiguration = initialConfiguration
        private val validatorBuilders: JsObjectValidators.Builder = JsObjectValidators.Builder(initialValidatorBuilders)
        private val properties = mutableListOf<JsReaderProperty<*>>()

        override fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit) {
            configuration = ObjectReaderConfiguration.Builder(configuration).apply(init).build()
        }

        override fun validation(init: JsObjectValidators.Builder.() -> Unit) {
            validatorBuilders.apply(init)
        }

        override fun <P : Any> property(name: String, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinderInstance(JsPath.Root / name, reader)

        override fun <P : Any> property(path: JsPath.Identifiable, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinderInstance(path, reader)

        override fun build(builder: (ObjectValuesMap) -> T): TypeBuilder<T> =
            TypeBuilder { v, p -> JsResult.Success(builder(v), p) }

        override fun build(builder: (ObjectValuesMap, JsResultPath) -> JsResult<T>): TypeBuilder<T> =
            TypeBuilder { v, p -> builder(v, p) }

        fun build(typeBuilder: TypeBuilder<T>): JsReader<T> {
            val validators = JsObjectValidatorInstances.of(validatorBuilders.build(), configuration, properties)
            return JsReader { context, path, input ->
                input.readAsObject(path, invalidTypeErrorBuilder) { p, b ->
                    read(configuration, validators, properties, typeBuilder, context, p, b)
                }
            }
        }

        private inner class PropertyBinderInstance<P : Any>(
            private val attributePath: JsPath.Identifiable,
            private val reader: JsReader<P>
        ) : PropertyBinder<P> {

            override fun required(): RequiredProperty<P> =
                RequiredPropertyInstance.of(attributePath, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun defaultable(default: () -> P): DefaultableProperty<P> =
                DefaultablePropertyInstance.of(attributePath, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun optional(): OptionalProperty<P> =
                OptionalPropertyInstance.of(attributePath, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun optional(default: () -> P): OptionalWithDefaultProperty<P> =
                OptionalWithDefaultPropertyInstance.of(attributePath, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun nullable(): NullableProperty<P> =
                NullablePropertyInstance.of(attributePath, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun nullable(default: () -> P): NullableWithDefaultProperty<P> =
                NullableWithDefaultPropertyInstance.of(attributePath, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun <P : Any> registration(property: JsReaderProperty<P>) {
                properties.add(property)
            }
        }
    }

    companion object {

        internal fun <T> read(
            configuration: ObjectReaderConfiguration,
            validators: JsObjectValidatorInstances,
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
            val objectValuesMap = ObjectValuesMap.Builder(context, currentPath, input)
                .apply {
                    properties.forEach { property ->
                        tryAddValueBy(property)
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
            validators: JsObjectValidatorInstances,
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
            validators: JsObjectValidatorInstances,
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
