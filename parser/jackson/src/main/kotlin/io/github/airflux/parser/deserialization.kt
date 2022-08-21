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

package io.github.airflux.parser

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.withCatching
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.value.deserialization
import io.github.airflux.serialization.dsl.writer.serialization

public fun <T : Any> String.deserialization(
    mapper: ObjectMapper,
    context: ReaderContext,
    reader: Reader<T>
): ReaderResult<T> =
    withCatching(context, location = Location.empty) {
        mapper.readValue(this, ValueNode::class.java).deserialization(context, reader)
    }

public fun <T : Any> T.serialization(
    mapper: ObjectMapper,
    context: WriterContext,
    writer: Writer<T>
): String? = this.serialization(context, Location.empty, writer)
    ?.let { mapper.writeValueAsString(it) }
