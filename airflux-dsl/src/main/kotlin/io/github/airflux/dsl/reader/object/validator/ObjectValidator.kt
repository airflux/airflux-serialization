package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

interface ObjectValidator {

    interface Identifier {
        val key: String
    }

    interface Before : ObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            context: JsReaderContext?
        ): List<JsError>

        interface Builder : Identifier {
            fun build(configuration: ObjectReaderConfiguration, properties: List<JsReaderProperty<*>>): Before
        }
    }

    interface After : ObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext?
        ): List<JsError>

        interface Builder : Identifier {
            fun build(configuration: ObjectReaderConfiguration, properties: List<JsReaderProperty<*>>): After
        }
    }
}
