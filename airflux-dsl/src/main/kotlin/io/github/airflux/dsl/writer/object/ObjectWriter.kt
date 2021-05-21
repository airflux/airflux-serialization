package io.github.airflux.dsl.writer.`object`

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.writer.`object`.property.JsWriterProperty
import io.github.airflux.dsl.writer.`object`.property.JsWriterPropertyBuilder
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsArrayWriter
import io.github.airflux.writer.JsObjectWriter
import io.github.airflux.writer.JsWriter

class ObjectWriter(private val configuration: ObjectWriterConfiguration) {

    operator fun <T : Any> invoke(init: Builder<T>.() -> Unit): JsObjectWriter<T> =
        Builder<T>().apply(init).build(configuration)

    @AirfluxMarker
    class Builder<T : Any> internal constructor(){
        private val properties = mutableListOf<JsWriterPropertyBuilder<T>>()

        fun <P : Any> requiredProperty(name: String, from: (T) -> P, writer: JsWriter<P>) {
            JsWriterPropertyBuilder.Required(name, from, writer).also { properties.add(it) }
        }

        fun <P : Any> optionalProperty(
            name: String,
            from: (T) -> P?,
            writer: JsWriter<P>
        ): JsWriterProperty.Optional.Simple<T, P> =
            JsWriterPropertyBuilder.Optional.Simple(name, from, writer).also { properties.add(it) }

        fun <P : Any> optionalProperty(
            name: String,
            from: (T) -> P?,
            writer: JsArrayWriter<P>
        ): JsWriterProperty.Optional.Array<T, P> =
            JsWriterPropertyBuilder.Optional.Array(name, from, writer).also { properties.add(it) }

        fun <P : Any> optionalProperty(
            name: String,
            from: (T) -> P?,
            writer: JsObjectWriter<P>
        ): JsWriterProperty.Optional.Object<T, P> =
            JsWriterPropertyBuilder.Optional.Object(name, from, writer).also { properties.add(it) }

        fun <P : Any> nullableProperty(
            name: String,
            from: (T) -> P?,
            writer: JsWriter<P>
        ): JsWriterProperty.Nullable.Simple<T, P> =
            JsWriterPropertyBuilder.Nullable.Simple(name, from, writer).also { properties.add(it) }

        fun <P : Any> nullableProperty(
            name: String,
            from: (T) -> P?,
            writer: JsArrayWriter<P>
        ): JsWriterProperty.Nullable.Array<T, P> =
            JsWriterPropertyBuilder.Nullable.Array(name, from, writer).also { properties.add(it) }

        fun <P : Any> nullableProperty(
            name: String,
            from: (T) -> P?,
            writer: JsObjectWriter<P>
        ): JsWriterProperty.Nullable.Object<T, P> =
            JsWriterPropertyBuilder.Nullable.Object(name, from, writer).also { properties.add(it) }

        internal fun build(configuration: ObjectWriterConfiguration): JsObjectWriter<T> {
            val propertiesByName = properties.asSequence()
                .map { property -> property.name to property.buildConverter(configuration) }
                .toMap()

            return JsObjectWriter { value ->
                mutableMapOf<String, JsValue>()
                    .apply {
                        propertiesByName.forEach { (name, converter) ->
                            converter(value)
                                ?.also { this[name] = it }
                        }
                    }
                    .let { JsObject(it) }
            }
        }
    }
}
