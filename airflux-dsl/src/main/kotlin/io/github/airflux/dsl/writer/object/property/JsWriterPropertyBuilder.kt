package io.github.airflux.dsl.writer.`object`.property

import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsArrayWriter
import io.github.airflux.writer.JsObjectWriter
import io.github.airflux.writer.JsWriter
import io.github.airflux.writer.extension.writeAsNullable
import io.github.airflux.writer.extension.writeAsOptional
import io.github.airflux.writer.extension.writeAsRequired

internal interface JsWriterPropertyBuilder<T> {

    val name: String
    fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue?

    class Required<T, P : Any>(
        override val name: String,
        private val getter: (T) -> P,
        private val writer: JsWriter<P>
    ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Required<T, P> {

        override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? = { value: T ->
            writeAsRequired(value, getter, writer)
        }
    }

    class Optional {
        internal class Simple<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsWriter<P>
        ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Optional.Simple<T, P> {

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? =
                buildConverter(getter, writer)
        }

        internal class Array<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsArrayWriter<P>
        ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Optional.Array<T, P> {
            private var skipIfEmpty: Boolean? = null

            override fun skipIfEmpty() {
                skipIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val skipIfEmpty: Boolean = skipIfEmpty ?: configuration.skipPropertyIfArrayIsEmpty
                return if (skipIfEmpty)
                    buildConverterIfEmptyResult(getter, writer)
                else
                    buildConverter(getter, writer)
            }
        }

        internal class Object<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsObjectWriter<P>
        ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Optional.Object<T, P> {
            private var skipIfEmpty: Boolean? = null

            override fun skipIfEmpty() {
                skipIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val skipIfEmpty = skipIfEmpty ?: configuration.skipPropertyIfObjectIsEmpty
                return if (skipIfEmpty)
                    buildConverterIfEmptyResult(getter, writer)
                else
                    buildConverter(getter, writer)
            }
        }

        companion object {

            fun <T, P : Any> buildConverter(getter: (T) -> P?, writer: JsWriter<P>): (T) -> JsValue? =
                { value: T -> writeAsOptional(value, getter, writer) }

            fun <T, P : Any> buildConverterIfEmptyResult(getter: (T) -> P?, writer: JsWriter<P>): (T) -> JsValue? =
                { value: T ->
                    val result = writeAsOptional(value, getter, writer)
                    if (result != null && result.isEmpty) null else result
                }
        }
    }

    class Nullable {

        internal class Simple<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsWriter<P>
        ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Nullable.Simple<T, P> {

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? =
                buildConverter(getter, writer)
        }

        internal class Array<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsArrayWriter<P>
        ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Nullable.Array<T, P> {

            private var nullIfEmpty: Boolean? = null

            override fun writeNullIfEmpty() {
                nullIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val nullIfEmpty: Boolean = nullIfEmpty ?: configuration.writeNullIfArrayIsEmpty
                return if (nullIfEmpty) buildConverterIfEmptyResult(getter, writer) else buildConverter(getter, writer)
            }
        }

        internal class Object<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsObjectWriter<P>
        ) : JsWriterPropertyBuilder<T>, JsWriterProperty.Nullable.Object<T, P> {

            private var nullIfEmpty: Boolean? = null

            override fun writeNullIfEmpty() {
                nullIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val nullIfEmpty: Boolean = nullIfEmpty ?: configuration.writeNullIfObjectIsEmpty
                return if (nullIfEmpty) buildConverterIfEmptyResult(getter, writer) else buildConverter(getter, writer)
            }
        }

        companion object {

            internal fun <T, P : Any> buildConverter(getter: (T) -> P?, writer: JsWriter<P>): (T) -> JsValue? =
                { value: T -> writeAsNullable(value, getter, writer) }

            internal fun <T, P : Any> buildConverterIfEmptyResult(
                getter: (T) -> P?,
                writer: JsWriter<P>
            ): (T) -> JsValue? =
                { value: T ->
                    val result = writeAsNullable(value, getter, writer)
                    if (result.isEmpty) JsNull else result
                }
        }
    }

    companion object {

        internal val JsValue.isEmpty: Boolean
            get() = when {
                this is JsArray<*> && this.underlying.isEmpty() -> true
                this is JsObject && this.underlying.isEmpty() -> true
                else -> false
            }
    }
}
