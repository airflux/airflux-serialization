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

package io.github.airflux.serialization.dsl.writer.`object`.builder.property

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.context.JsWriterContext
import io.github.airflux.serialization.core.writer.`object`.writeNonNullable
import io.github.airflux.serialization.core.writer.`object`.writeNullable
import io.github.airflux.serialization.core.writer.`object`.writeOptional
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification.JsObjectPropertySpec

public sealed class JsObjectProperty<T : Any> {
    public abstract val name: String
    public abstract fun write(context: JsWriterContext, location: JsLocation, input: T): ValueNode?

    public class NonNullable<T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P,
        private val writer: JsWriter<P>
    ) : JsObjectProperty<T>() {

        internal constructor(spec: JsObjectPropertySpec.NonNullable<T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(context: JsWriterContext, location: JsLocation, input: T): ValueNode? =
            writeNonNullable(context = context, location = location, using = writer, value = from(input))
    }

    public class Optional<T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P?,
        private val writer: JsWriter<P>
    ) : JsObjectProperty<T>() {

        internal constructor(spec: JsObjectPropertySpec.Optional<T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(context: JsWriterContext, location: JsLocation, input: T): ValueNode? =
            writeOptional(context = context, location = location, using = writer, value = from(input))
    }

    public class Nullable<T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P?,
        private val writer: JsWriter<P>
    ) : JsObjectProperty<T>() {

        internal constructor(spec: JsObjectPropertySpec.Nullable<T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(context: JsWriterContext, location: JsLocation, input: T): ValueNode? =
            writeNullable(context = context, location = location, using = writer, value = from(input))
    }
}
