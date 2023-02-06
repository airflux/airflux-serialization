/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.writer.struct.property

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.writeNonNullable
import io.github.airflux.serialization.core.writer.writeNullable
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec

public sealed class StructProperty<O, CTX, T : Any> {
    public abstract val name: String
    public abstract fun write(env: WriterEnv<O>, context: CTX, location: Location, value: T): ValueNode?

    public class NonNullable<O, CTX, T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P,
        private val writer: Writer<O, CTX, P>
    ) : StructProperty<O, CTX, T>() {

        internal constructor(spec: StructPropertySpec.NonNullable<O, CTX, T, P>) : this(
            spec.name,
            spec.from,
            spec.writer
        )

        override fun write(env: WriterEnv<O>, context: CTX, location: Location, value: T): ValueNode? =
            writeNonNullable(env = env, context = context, location = location, using = writer, value = from(value))
    }

    public class Nullable<O, CTX, T : Any, P : Any> private constructor(
        override val name: String,
        private val from: (T) -> P?,
        private val writer: Writer<O, CTX, P>
    ) : StructProperty<O, CTX, T>() {

        internal constructor(spec: StructPropertySpec.Nullable<O, CTX, T, P>) : this(spec.name, spec.from, spec.writer)

        override fun write(env: WriterEnv<O>, context: CTX, location: Location, value: T): ValueNode? =
            writeNullable(env = env, context = context, location = location, using = writer, value = from(value))
    }
}
