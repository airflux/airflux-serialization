package io.github.airflux.dsl.reader.`object`

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
            attributes: List<Attribute<*>>
        ): List<JsError>

        interface Builder : Identifier {
            fun build(configuration: ObjectReaderConfiguration, attributes: List<Attribute<*>>): Before
        }
    }

    interface After : ObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            attributes: List<Attribute<*>>,
            objectValuesMap: ObjectValuesMap
        ): List<JsError>

        interface Builder : Identifier {
            fun build(configuration: ObjectReaderConfiguration, attributes: List<Attribute<*>>): After
        }
    }
}
