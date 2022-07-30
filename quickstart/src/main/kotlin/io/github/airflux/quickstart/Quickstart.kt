package io.github.airflux.quickstart

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.parser.AirFluxJsonModule
import io.github.airflux.quickstart.dto.Response
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.reader.context.DefaultReaderContext
import io.github.airflux.quickstart.dto.reader.dsl.RequestReader
import io.github.airflux.quickstart.dto.writer.ResponseWriter
import io.github.airflux.quickstart.dto.writer.context.DefaultWriterContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.value.deserialization
import io.github.airflux.serialization.dsl.writer.serialization
import java.math.BigDecimal

fun main() {
    val mapper = ObjectMapper().apply {
        registerModule(AirFluxJsonModule)
    }

    val json = mapper.readValue(jsonOfTender, ValueNode::class.java)

    when (val result = json.deserialization(context = DefaultReaderContext, reader = RequestReader)) {
        is JsResult.Success -> println(result.value)
        is JsResult.Failure -> println(result.causes)
    }

    val value = Value(amount = BigDecimal("125.52"), currency = "USD")
    val lot = Lot(id = "lot-1", status = LotStatus.ACTIVE, value = value)
    val tender = Tender(id = "tender-1", title = "title", value = value, lots = listOf(lot))
    val response = Response(tender = tender)
    val output: ValueNode? = response.serialization(DefaultWriterContext, JsLocation.empty, ResponseWriter)
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
