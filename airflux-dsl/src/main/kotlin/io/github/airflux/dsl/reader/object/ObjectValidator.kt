package io.github.airflux.dsl.reader.`object`

import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

interface ObjectValidator<E : JsError> {

    interface Identifier {
        val key: String
    }

    interface Before<E : JsError> : ObjectValidator<E> {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            attributes: List<Attribute<*, E>>
        ): List<E>

        interface Builder<E : JsError> : Identifier {
            fun build(configuration: ObjectReaderConfiguration, attributes: List<Attribute<*, E>>): Before<E>
        }
    }

    interface After<E : JsError> : ObjectValidator<E> {

        fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            attributes: List<Attribute<*, E>>,
            objectValuesMap: ObjectValuesMap<E>
        ): List<E>

        interface Builder<E : JsError> : Identifier {
            fun build(configuration: ObjectReaderConfiguration, attributes: List<Attribute<*, E>>): After<E>
        }
    }
}
