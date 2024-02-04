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

package io.github.airflux.quickstart.infrastructure.web.model.writer.base

import io.github.airflux.quickstart.domain.model.Amount
import io.github.airflux.quickstart.domain.model.Currency
import io.github.airflux.quickstart.infrastructure.web.model.writer.env.WriterOptions
import io.github.airflux.serialization.core.writer.contramap
import io.github.airflux.serialization.std.writer.bigDecimalWriter
import io.github.airflux.serialization.std.writer.stringWriter

val StringWriter = stringWriter<WriterOptions>()
val BigDecimalWriter = bigDecimalWriter<WriterOptions>(stripTrailingZeros = false)

val AmountWriter = BigDecimalWriter.contramap { value: Amount -> value.get }
val CurrencyWriter = StringWriter.contramap { value: Currency -> value.get }
