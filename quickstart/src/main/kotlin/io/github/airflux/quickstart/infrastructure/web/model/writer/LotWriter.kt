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

package io.github.airflux.quickstart.infrastructure.web.model.writer

import io.github.airflux.quickstart.domain.model.Lot
import io.github.airflux.quickstart.domain.model.LotStatus
import io.github.airflux.quickstart.infrastructure.web.model.writer.base.StringWriter
import io.github.airflux.quickstart.infrastructure.web.model.writer.env.WriterOptions
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.optional
import io.github.airflux.serialization.dsl.writer.array.arrayWriter
import io.github.airflux.serialization.dsl.writer.struct.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.property.specification.nullable
import io.github.airflux.serialization.dsl.writer.struct.structWriter

val LotStatusWriter = JsWriter<WriterOptions, LotStatus> { _, _, _, value ->
    JsString(value.name)
}

val LotWriter: JsWriter<WriterOptions, Lot> = structWriter {
    property(nonNullable(name = "id", from = Lot::id, StringWriter))
    property(nonNullable(name = "status", from = { -> status }, writer = LotStatusWriter))
    property(nullable(name = "value", from = { -> value }, writer = ValueWriter.optional()))
}

val LotsWriter = arrayWriter(items = LotWriter)
