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

package io.github.airflux.quickstart.infrastructure.web.model.reader

import io.github.airflux.quickstart.domain.model.Value
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderCtx
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.CommonStructReaderValidators
import io.github.airflux.quickstart.infrastructure.web.model.reader.validator.additionalProperties
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader

val ValueReader: Reader<ReaderErrorBuilders, ReaderCtx, Value> = structReader {

    validation {
        +CommonStructReaderValidators
        +additionalProperties
    }

    val amount = property(required(name = "amount", reader = AmountReader))
    val currency = property(required(name = "currency", reader = CurrencyReader))

    returns { _, _ ->
        Value(amount = +amount, currency = +currency).success()
    }
}