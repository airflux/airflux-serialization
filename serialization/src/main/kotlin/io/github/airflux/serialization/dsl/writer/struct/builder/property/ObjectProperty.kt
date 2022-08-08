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

package io.github.airflux.serialization.dsl.writer.struct.builder.property

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.core.writer.struct.writeNonNullable
import io.github.airflux.serialization.core.writer.struct.writeNullable
import io.github.airflux.serialization.core.writer.struct.writeOptional
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.ObjectPropertySpec

public sealed class ObjectProperty<T : Any> {
    public abstract val name: String
    public abstract fun write(context: WriterContext, location: Location, value: T): ValueNode?

    public class NonNullable<T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P,
        private val writer: Writer<P>
    ) : ObjectProperty<T>() {

        internal constructor(spec: ObjectPropertySpec.NonNullable<T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(context: WriterContext, location: Location, value: T): ValueNode? =
            writeNonNullable(context = context, location = location, using = writer, value = from(value))
    }

    public class Optional<T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P?,
        private val writer: Writer<P>
    ) : ObjectProperty<T>() {

        internal constructor(spec: ObjectPropertySpec.Optional<T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(context: WriterContext, location: Location, value: T): ValueNode? =
            writeOptional(context = context, location = location, using = writer, value = from(value))
    }

    public class Nullable<T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P?,
        private val writer: Writer<P>
    ) : ObjectProperty<T>() {

        internal constructor(spec: ObjectPropertySpec.Nullable<T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(context: WriterContext, location: Location, value: T): ValueNode? =
            writeNullable(context = context, location = location, using = writer, value = from(value))
    }
}
