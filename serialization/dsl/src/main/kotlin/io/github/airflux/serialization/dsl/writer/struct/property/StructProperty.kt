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

import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.dsl.writer.struct.property.specification.StructPropertySpec

public class StructProperty<O, T, P> private constructor(
    public val name: String,
    private val writer: JsWriter<O, T>
) {

    internal constructor(spec: StructPropertySpec<O, T, P>) : this(name = spec.name, writer = createWriter(spec))

    public fun write(env: JsWriterEnv<O>, context: JsContext, location: JsLocation, source: T): JsValue? =
        writer.write(env, context, location, source)

    internal companion object {

        @JvmStatic
        private fun <O, T, P> createWriter(spec: StructPropertySpec<O, T, P>): JsWriter<O, T> {
            val writer = spec.writer

            return when (spec.from) {
                is StructPropertySpec.Extractor.WithoutContext -> {
                    val extractor = spec.from.extractor
                    JsWriter { env, context, location, source ->
                        writer.write(env, context, location, source.extractor())
                    }
                }

                is StructPropertySpec.Extractor.WithContext -> {
                    val extractor = spec.from.extractor
                    JsWriter { env, context, location, source ->
                        writer.write(env, context, location, source.extractor(context))
                    }
                }
            }
        }
    }
}
