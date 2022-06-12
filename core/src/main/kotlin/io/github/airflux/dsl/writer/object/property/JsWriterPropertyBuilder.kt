/*
 * Copyright 2021-2022 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.dsl.writer.`object`.property

import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsArrayWriter
import io.github.airflux.core.writer.JsObjectWriter
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.extension.writeAsNullable
import io.github.airflux.core.writer.extension.writeAsOptional
import io.github.airflux.core.writer.extension.writeAsRequired
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration

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
                this is JsArray<*> && this.isEmpty() -> true
                this is JsObject && this.isEmpty() -> true
                else -> false
            }
    }
}