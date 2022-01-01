package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.ObjectReader.TypeBuilder
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
import io.github.airflux.reader.result.JsErrors
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.readAsObject

@Suppress("unused")
fun <T : Any> JsValue.deserialization(context: JsReaderContext = JsReaderContext(), reader: JsReader<T>): JsResult<T> =
    reader.read(context, JsLocation.Root, this)

@Suppress("unused")
class ObjectReader(
    private val globalConfiguration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
    private val globalValidators: JsObjectValidators = JsObjectValidators.Default,
    private val pathMissingErrorBuilder: PathMissingErrorBuilder,
    private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
) {

    operator fun <T> invoke(
        configuration: ObjectReaderConfiguration? = null,
        validators: JsObjectValidators? = null,
        init: Builder<T>.() -> TypeBuilder<T>
    ): JsReader<T> {
        val builder = BuilderInstance<T>(configuration ?: globalConfiguration, validators ?: globalValidators)
        val typeBuilder = builder.init()
        return builder.build(typeBuilder)
    }

    @AirfluxMarker
    interface Builder<T> {
        fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit)
        fun validation(init: JsObjectValidators.Builder.() -> Unit)

        fun <P : Any> property(name: String, reader: JsReader<P>): PropertyBinder<P>
        fun <P : Any> property(path: JsPath.Identifiable, reader: JsReader<P>): PropertyBinder<P>

        fun build(builder: ObjectValuesMap.(JsLocation) -> JsResult<T>): TypeBuilder<T>
        fun build(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): TypeBuilder<T>
    }

    interface PropertyBinder<P : Any> {
        fun required(): RequiredProperty<P>
        fun defaultable(default: () -> P): DefaultableProperty<P>

        fun optional(): OptionalProperty<P>
        fun optional(default: () -> P): OptionalWithDefaultProperty<P>

        fun nullable(): NullableProperty<P>
        fun nullable(default: () -> P): NullableWithDefaultProperty<P>
    }

    fun interface TypeBuilder<T> : (JsReaderContext, ObjectValuesMap, JsLocation) -> JsResult<T>

    private inner class BuilderInstance<T>(
        private var configuration: ObjectReaderConfiguration,
        validators: JsObjectValidators
    ) : Builder<T> {

        private val validatorBuilders: JsObjectValidators.Builder = JsObjectValidators.Builder(validators)
        private val properties = mutableListOf<JsReaderProperty>()

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

        override fun build(builder: ObjectValuesMap.(JsLocation) -> JsResult<T>): TypeBuilder<T> =
            TypeBuilder { _, v, p -> v.builder(p) }

        override fun build(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): TypeBuilder<T> =
            TypeBuilder { c, v, p -> v.builder(c, p) }

        fun build(typeBuilder: TypeBuilder<T>): JsReader<T> {
            val validators = JsObjectValidatorInstances.of(validatorBuilders.build(), configuration, properties)
            return JsReader { context, location, input ->
                input.readAsObject(location, invalidTypeErrorBuilder) { p, b ->
                    read(configuration, validators, properties, typeBuilder, context, p, b)
                }
            }
        }

        private inner class PropertyBinderInstance<P : Any>(
            private val path: JsPath.Identifiable,
            private val reader: JsReader<P>
        ) : PropertyBinder<P> {

            override fun required(): RequiredProperty<P> =
                RequiredPropertyInstance.of(path, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun defaultable(default: () -> P): DefaultableProperty<P> =
                DefaultablePropertyInstance.of(path, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun optional(): OptionalProperty<P> =
                OptionalPropertyInstance.of(path, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun optional(default: () -> P): OptionalWithDefaultProperty<P> =
                OptionalWithDefaultPropertyInstance.of(path, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun nullable(): NullableProperty<P> =
                NullablePropertyInstance.of(path, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun nullable(default: () -> P): NullableWithDefaultProperty<P> =
                NullableWithDefaultPropertyInstance.of(path, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun registration(property: JsReaderProperty) {
                properties.add(property)
            }
        }
    }

    companion object {

        internal fun <T> read(
            configuration: ObjectReaderConfiguration,
            validators: JsObjectValidatorInstances,
            properties: List<JsReaderProperty>,
            typeBuilder: TypeBuilder<T>,
            context: JsReaderContext,
            location: JsLocation,
            input: JsObject
        ): JsResult<T> {
            val failures = mutableListOf<JsResult.Failure>()

            val preValidationErrors = preValidation(configuration, input, validators, properties, context)
            if (preValidationErrors != null) {
                val hasCriticalError = preValidationErrors.hasCritical()
                val failure = JsResult.Failure(location, preValidationErrors)
                if (configuration.failFast || hasCriticalError)
                    return failure
                else
                    failures.add(failure)
            }

            val objectValuesMap = ObjectValuesMap.Builder(context, location, input)
                .apply {
                    properties.forEach { property ->
                        val failure = tryAddValueBy(property)
                        if (failure != null) {
                            val hasCriticalError = failure.causes
                                .any { (_, errors) -> errors.hasCritical() }
                            failures.add(failure)
                            if (configuration.failFast || hasCriticalError) return failures.merge()
                        }
                    }
                }
                .build()

            val postValidationErrors =
                postValidation(configuration, input, validators, properties, objectValuesMap, context)
            if (postValidationErrors != null) {
                val error = JsResult.Failure(location, postValidationErrors)
                failures.add(error)
            }

            return if (failures.isEmpty())
                typeBuilder(context, objectValuesMap, location)
            else
                failures.merge()
        }

        internal fun preValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validators: JsObjectValidatorInstances,
            properties: List<JsReaderProperty>,
            context: JsReaderContext
        ): JsErrors? = mutableListOf<JsError>()
            .apply {
                validators.before
                    .forEach { validator ->
                        val validationResult = validator.validation(configuration, input, properties, context)
                        if (validationResult != null) {
                            val hasCriticalError = validationResult.hasCritical()
                            addAll(validationResult)
                            if (configuration.failFast || hasCriticalError) return@forEach
                        }
                    }
            }
            .let { JsErrors.of(it) }

        internal fun postValidation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            validators: JsObjectValidatorInstances,
            properties: List<JsReaderProperty>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext
        ): JsErrors? = mutableListOf<JsError>()
            .apply {
                validators.after
                    .forEach { validator ->
                        val validationResult =
                            validator.validation(configuration, input, properties, objectValuesMap, context)
                        if (validationResult != null) {
                            val hasCriticalError = validationResult.hasCritical()
                            addAll(validationResult)
                            if (configuration.failFast || hasCriticalError) return@forEach
                        }
                    }
            }
            .let { JsErrors.of(it) }
    }
}