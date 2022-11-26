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

package io.github.airflux.quickstart

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.parser.AirFluxJsonModule
import io.github.airflux.parser.deserialization
import io.github.airflux.parser.serialization
import io.github.airflux.quickstart.dto.Response
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.model.Lots
import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.reader.dsl.RequestReader
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.quickstart.dto.writer.ResponseWriter
import io.github.airflux.quickstart.dto.writer.env.WriterCtx
import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.dsl.reader.env.exception.exceptionsHandler
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty
import java.math.BigDecimal

fun main() {
    val mapper = ObjectMapper().apply {
        registerModule(AirFluxJsonModule)
    }

    val env =
        ReaderEnv(
            errorBuilders = ReaderErrorBuilders,
            context = ReaderCtx(failFast = true),
            exceptionsHandler = exceptionsHandler {
                exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
                exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
            }
        )

    val result = JSON.deserialization(mapper = mapper, env = env, reader = RequestReader)
    when (result) {
        is ReaderResult.Success -> println(result.value)
        is ReaderResult.Failure -> println(result.causes)
    }

    val value = Value(amount = BigDecimal("125.52"), currency = "USD")
    val lot = Lot(id = "lot-1", status = LotStatus.ACTIVE, value = value)
    val tender = Tender(id = "tender-1", title = "title", value = value, lots = Lots(listOf(lot)))
    val response = Response(tender = tender)

    val writerEnv =
        WriterEnv(context = WriterCtx(writerActionIfResultIsEmpty = WriterActionIfResultIsEmpty.RETURN_NOTHING))
    val output = response.serialization(mapper, writerEnv, ResponseWriter)
    println(output)
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
