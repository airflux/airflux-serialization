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

package io.github.airflux.quickstart.infrastructure.web.model.writer

import io.github.airflux.quickstart.domain.model.Value
import io.github.airflux.quickstart.infrastructure.web.model.writer.base.amountWriter
import io.github.airflux.quickstart.infrastructure.web.model.writer.base.currencyWriter
import io.github.airflux.quickstart.infrastructure.web.model.writer.env.WriterOptions
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.struct.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.structWriter

val ValueWriter: JsWriter<WriterOptions, Value> = structWriter {
    property(nonNullable(name = "amount", from = { -> amount }, writer = amountWriter))
    property(nonNullable(name = "currency", from = { -> currency }, writer = currencyWriter))
}
