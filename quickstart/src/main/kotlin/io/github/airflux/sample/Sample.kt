package io.github.airflux.sample

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.dsl.reader.`object`.deserialization
import io.github.airflux.dsl.writer.`object`.serialization
import io.github.airflux.parser.AirFluxJsonModule
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.Response
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.dto.model.LotStatus
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.dsl.RequestReader
import io.github.airflux.sample.dto.writer.ResponseWriter
import io.github.airflux.value.JsValue
import java.math.BigDecimal

fun main() {
    val mapper = ObjectMapper().apply {
        registerModule(AirFluxJsonModule)
    }

    val json = mapper.readValue(jsonOfTender, JsValue::class.java)

    when (val result = json.deserialization(reader = RequestReader)) {
        is JsResult.Success -> println(result.value)
        is JsResult.Failure -> println(result.errors)
    }

    val value = Value(amount = BigDecimal("125.52"), currency = "USD")
    val lot = Lot(id = "lot-1", status = LotStatus.ACTIVE, value = value)
    val tender = Tender(id = "tender-1", title = "title", value = value, lots = listOf(lot))
    val response = Response(tender = tender)
    val output: JsValue = response.serialization(ResponseWriter)
    println(output.toString())
}

const val jsonOfTender = """{
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
