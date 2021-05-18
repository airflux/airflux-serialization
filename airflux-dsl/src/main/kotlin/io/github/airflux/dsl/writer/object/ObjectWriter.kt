package io.github.airflux.dsl.writer.`object`

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsArrayWriter
import io.github.airflux.writer.JsObjectWriter
import io.github.airflux.writer.JsWriter

class ObjectWriter(private val configuration: ObjectWriterConfiguration) {

    operator fun <T : Any> invoke(init: Builder<T>.() -> Unit): JsObjectWriter<T> =
        Builder<T>().apply(init).build(configuration)

    @AirfluxMarker
    class Builder<T : Any> {
        private val properties = mutableListOf<JsProperty<T, *>>()

        fun <P : Any> requiredProperty(name: String, from: (T) -> P, writer: JsWriter<P>) {
            JsProperty.Required(name, from, writer).also { properties.add(it) }
        }

        fun <P : Any> optionalProperty(name: String, from: (T) -> P?, writer: JsWriter<P>) =
            JsProperty.Optional.Simple(name, from, writer).also { properties.add(it) }

        fun <P : Any> optionalProperty(name: String, from: (T) -> P?, writer: JsArrayWriter<P>) =
            JsProperty.Optional.Array(name, from, writer).also { properties.add(it) }

        fun <P : Any> optionalProperty(name: String, from: (T) -> P?, writer: JsObjectWriter<P>) =
            JsProperty.Optional.Object(name, from, writer).also { properties.add(it) }

        fun <P : Any> nullableProperty(name: String, from: (T) -> P?, writer: JsWriter<P>) =
            JsProperty.Nullable.Simple(name, from, writer).also { properties.add(it) }

        fun <P : Any> nullableProperty(name: String, from: (T) -> P?, writer: JsArrayWriter<P>) =
            JsProperty.Nullable.Array(name, from, writer).also { properties.add(it) }

        fun <P : Any> nullableProperty(name: String, from: (T) -> P?, writer: JsObjectWriter<P>) =
            JsProperty.Nullable.Object(name, from, writer).also { properties.add(it) }

        internal fun build(configuration: ObjectWriterConfiguration): JsObjectWriter<T> {
            val propertiesByName = properties.asSequence()
                .map { property -> property.name to property.buildConverter(configuration) }
                .toMap()

            return JsObjectWriter { value ->
                mutableMapOf<String, JsValue>()
                    .apply {
                        propertiesByName.forEach { (name, converter) ->
                            converter(value)?.also { this[name] = it }
                        }
                    }
                    .let { JsObject(it) }
            }
        }
    }
}
