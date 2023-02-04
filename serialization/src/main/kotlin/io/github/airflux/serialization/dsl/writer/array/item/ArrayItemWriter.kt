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

package io.github.airflux.serialization.dsl.writer.array.item

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.writeNonNullable
import io.github.airflux.serialization.core.writer.writeNullable
import io.github.airflux.serialization.dsl.writer.array.item.specification.ArrayItemSpec

public sealed class ArrayItemWriter<O, CTX, T> {

    public abstract fun write(env: WriterEnv<O>, context: CTX, location: Location, value: T): ValueNode?

    public class NonNullable<O, CTX, T : Any> private constructor(
        private val writer: Writer<O, CTX, T>
    ) : ArrayItemWriter<O, CTX, T>() {

        internal constructor(spec: ArrayItemSpec.NonNullable<O, CTX, T>) : this(spec.writer)

        override fun write(env: WriterEnv<O>, context: CTX, location: Location, value: T): ValueNode? =
            writeNonNullable(env = env, context = context, location = location, using = writer, value = value)
    }

    public class Nullable<O, CTX, T> private constructor(
        private val writer: Writer<O, CTX, T & Any>
    ) : ArrayItemWriter<O, CTX, T>() {

        internal constructor(spec: ArrayItemSpec.Nullable<O, CTX, T>) : this(spec.writer)

        override fun write(env: WriterEnv<O>, context: CTX, location: Location, value: T): ValueNode? =
            writeNullable(env = env, context = context, location = location, using = writer, value = value)
    }
}
