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

package io.github.airflux.quickstart.infrastructure.web.model.writer

import io.github.airflux.quickstart.domain.model.Lot
import io.github.airflux.quickstart.domain.model.LotStatus
import io.github.airflux.quickstart.infrastructure.web.model.writer.base.StringWriter
import io.github.airflux.quickstart.infrastructure.web.model.writer.env.WriterCtx
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.dsl.writer.array.builder.arrayWriter
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nullable
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.builder.structWriter

val LotStatus = Writer<WriterCtx, LotStatus> { _, _, value ->
    StringNode(value.name)
}

val LotWriter: Writer<WriterCtx, Lot> = structWriter {
    property(nonNullable(name = "id", from = Lot::id, StringWriter))
    property(nonNullable(name = "status", from = Lot::status, writer = LotStatus))
    property(nonNullable(name = "value", from = Lot::value, writer = ValueWriter))
}

val LotsWriter: Writer<WriterCtx, Iterable<Lot>> = arrayWriter {
    items(nullable(LotWriter))
}
