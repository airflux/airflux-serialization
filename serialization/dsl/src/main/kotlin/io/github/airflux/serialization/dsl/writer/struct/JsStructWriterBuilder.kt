/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.writer.struct

import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.struct.JsStructWriter
import io.github.airflux.serialization.core.writer.struct.buildStructWriter
import io.github.airflux.serialization.core.writer.struct.property.JsStructProperty
import io.github.airflux.serialization.core.writer.struct.property.specification.JsStructPropertySpec
import io.github.airflux.serialization.dsl.AirfluxMarker

public fun <O, T> structWriter(block: JsStructWriterBuilder<O, T>.() -> Unit): JsStructWriter<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption =
    JsStructWriterBuilder<O, T>().apply(block).build()

@AirfluxMarker
public class JsStructWriterBuilder<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption {

    private val properties = mutableListOf<JsStructProperty<O, T, *>>()

    public fun <P> property(spec: JsStructPropertySpec<O, T, P>): JsStructProperty<O, T, P> =
        JsStructProperty(spec).also { properties.add(it) }

    internal fun build(): JsStructWriter<O, T> = buildStructWriter(properties)
}
