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

package io.github.airflux.quickstart

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.quickstart.domain.model.Amount
import io.github.airflux.quickstart.domain.model.Currency
import io.github.airflux.quickstart.domain.model.Lot
import io.github.airflux.quickstart.domain.model.LotStatus
import io.github.airflux.quickstart.domain.model.Lots
import io.github.airflux.quickstart.domain.model.Tender
import io.github.airflux.quickstart.domain.model.Value
import io.github.airflux.quickstart.infrastructure.web.model.Response
import io.github.airflux.quickstart.infrastructure.web.model.reader.RequestReader
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.infrastructure.web.model.reader.env.ReaderOptions
import io.github.airflux.quickstart.infrastructure.web.model.writer.ResponseWriter
import io.github.airflux.quickstart.infrastructure.web.model.writer.env.WriterOptions
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.parser.AirFluxJsonModule
import io.github.airflux.serialization.parser.deserialization
import io.github.airflux.serialization.parser.serialization
import java.math.BigDecimal

fun main() {
    val mapper = ObjectMapper().apply {
        registerModule(AirFluxJsonModule)
    }

    val env = JsReaderEnv(
        config = JsReaderEnv.Config(
            errorBuilders = ReaderErrorBuilders,
            options = ReaderOptions(failFast = true)
        )
    )

    JSON.deserialization(mapper = mapper, env = env, reader = RequestReader)
        .fold(
            onSuccess = { result -> println(result.value) },
            onFailure = { result -> println(result.causes) }
        )

    val value = Value(amount = Amount(BigDecimal("125.52")), currency = Currency("USD"))
    val lot = Lot(id = "lot-1", status = LotStatus.ACTIVE, value = value)
    val tender = Tender(id = "tender-1", title = "title", value = value, lots = Lots(listOf(lot)))
    val response = Response(tender = tender)

    val writerEnv = JsWriterEnv(
        config = JsWriterEnv.Config(
            options = WriterOptions(writerActionIfResultIsEmpty = WriterActionIfResultIsEmpty.RETURN_NOTHING)
        )
    )
    val output = response.serialization(mapper, writerEnv, writer = ResponseWriter)
    println(output.orThrow())
}

const val JSON = """{
  "tender": {
    "id": "tender-md-0000-1234",
    "title": "Title of the tender.",
    "lots": [
      {
        "id": "lot-1",
        "status": "active",
        "value": {
          "amount": 123.78,
          "currency": "USD"
        }
      }
    ]
  }
}
"""
