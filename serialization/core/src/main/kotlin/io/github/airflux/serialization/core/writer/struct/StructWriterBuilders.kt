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

package io.github.airflux.serialization.core.writer.struct

import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.core.writer.struct.property.JsStructProperties

public fun <O, T> buildStructWriter(properties: JsStructProperties<O, T>): JsStructWriter<O, T>
    where O : WriterActionBuilderIfResultIsEmptyOption =
    JsStructWriter { env, location, source ->
        val struct = JsStruct.builder()
            .apply {
                properties.forEach { property ->
                    val currentLocation = location.append(property.name)
                    property.write(env, currentLocation, source)
                        ?.let { value -> add(name = property.name, value = value) }
                }
            }
            .build()

        if (struct.isEmpty())
            when (env.config.options.writerActionIfResultIsEmpty) {
                RETURN_EMPTY_VALUE -> struct
                RETURN_NOTHING -> null
                RETURN_NULL_VALUE -> JsNull
            }
        else
            struct
    }
