package io.github.airflux.dsl.writer.`object`

import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration.WriteProperty.OUTPUT_NULL_IF_ARRAY_IS_EMPTY
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration.WriteProperty.OUTPUT_NULL_IF_OBJECT_IS_EMPTY
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration.WriteProperty.SKIP_PROPERTY_IF_ARRAY_IS_EMPTY
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration.WriteProperty.SKIP_PROPERTY_IF_OBJECT_IS_EMPTY
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

@Suppress("unused")
sealed class JsProperty<T, P : Any> {

    internal abstract val name: String
    internal abstract fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue?

    class Required<T, P : Any>(
        override val name: String,
        private val getter: (T) -> P,
        private val writer: JsWriter<P>
    ) : JsProperty<T, P>() {

        override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? = { value: T ->
            writeAsRequired(value, getter, writer)
        }
    }

    sealed class Optional<T, P : Any> : JsProperty<T, P>() {

        class Simple<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsWriter<P>
        ) : Optional<T, P>() {

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? =
                buildConverter(getter, writer)
        }

        class Array<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsArrayWriter<P>
        ) : Optional<T, P>() {

            private var skipIfEmpty: Boolean? = null

            fun skipIfEmpty() {
                skipIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val skipIfEmpty: Boolean = skipIfEmpty ?: (SKIP_PROPERTY_IF_ARRAY_IS_EMPTY in configuration.properties)
                return if (skipIfEmpty) buildConverterIfEmptyResult(getter, writer) else buildConverter(getter, writer)
            }
        }

        class Object<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsObjectWriter<P>
        ) : Optional<T, P>() {

            private var skipIfEmpty: Boolean? = null

            fun skipIfEmpty() {
                skipIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val skipIfEmpty = skipIfEmpty ?: (SKIP_PROPERTY_IF_OBJECT_IS_EMPTY in configuration.properties)
                return if (skipIfEmpty) buildConverterIfEmptyResult(getter, writer) else buildConverter(getter, writer)
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

    sealed class Nullable<T, P : Any> : JsProperty<T, P>() {

        class Simple<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsWriter<P>
        ) : Nullable<T, P>() {

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? =
                buildConverter(getter, writer)
        }

        class Array<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsArrayWriter<P>
        ) : Nullable<T, P>() {

            private var nullIfEmpty: Boolean? = null

            fun nullIfEmpty() {
                nullIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val nullIfEmpty: Boolean = nullIfEmpty ?: (OUTPUT_NULL_IF_ARRAY_IS_EMPTY in configuration.properties)
                return if (nullIfEmpty) buildConverterIfEmptyResult(getter, writer) else buildConverter(getter, writer)
            }
        }

        class Object<T, P : Any>(
            override val name: String,
            private val getter: (T) -> P?,
            private val writer: JsObjectWriter<P>
        ) : Nullable<T, P>() {

            private var nullIfEmpty: Boolean? = null

            fun nullIfEmpty() {
                nullIfEmpty = true
            }

            override fun buildConverter(configuration: ObjectWriterConfiguration): (T) -> JsValue? {
                val nullIfEmpty: Boolean = nullIfEmpty ?: (OUTPUT_NULL_IF_OBJECT_IS_EMPTY in configuration.properties)
                return if (nullIfEmpty) buildConverterIfEmptyResult(getter, writer) else buildConverter(getter, writer)
            }
        }

        companion object {
            internal fun <T, P : Any> buildConverter(getter: (T) -> P?, writer: JsWriter<P>): (T) -> JsValue? =
                { value: T -> writeAsNullable(value, getter, writer) }

            internal fun <T, P : Any> buildConverterIfEmptyResult(getter: (T) -> P?, writer: JsWriter<P>): (T) -> JsValue? =
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
