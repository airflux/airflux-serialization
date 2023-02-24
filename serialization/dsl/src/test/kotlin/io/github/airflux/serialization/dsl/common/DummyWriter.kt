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

package io.github.airflux.serialization.dsl.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv

internal class DummyWriter<O, CTX, T : Any>(val result: (T) -> ValueNode?) : Writer<O, CTX, T> {
    override fun write(env: WriterEnv<O>, context: CTX, location: Location, source: T): ValueNode? = result(source)

    companion object {
        internal fun <O, CTX> intWriter(): Writer<O, CTX, Int> =
            DummyWriter(result = { source -> NumericNode.valueOf(source) })

        internal fun <O, CTX> stringWriter(): Writer<O, CTX, String> =
            DummyWriter(result = { source -> StringNode(source) })
    }
}
