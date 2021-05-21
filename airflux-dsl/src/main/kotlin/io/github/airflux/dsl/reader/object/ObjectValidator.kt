package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
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
            properties: List<JsReaderProperty<*>>
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
            objectValuesMap: ObjectValuesMap
        ): List<JsError>

        interface Builder : Identifier {
            fun build(configuration: ObjectReaderConfiguration, properties: List<JsReaderProperty<*>>): After
        }
    }
}
