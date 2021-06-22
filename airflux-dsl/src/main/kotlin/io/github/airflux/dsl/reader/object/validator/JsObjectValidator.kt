package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

sealed interface JsObjectValidator {

    interface Id<E : JsObjectValidator>

    interface Identifier {
        val id: Id<*>
    }

    sealed interface Builder<T : JsObjectValidator> {
        val id: Id<*>

        fun build(configuration: ObjectReaderConfiguration, properties: List<JsReaderProperty<*>>): T
    }

    interface Before : JsObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            context: JsReaderContext?
        ): List<JsError>

        interface Builder : JsObjectValidator.Builder<Before>
    }

    interface After : JsObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext?
        ): List<JsError>

        interface Builder : JsObjectValidator.Builder<After>
    }
}
