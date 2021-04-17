package io.github.airflux.sample

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.dsl.ReaderDsl.read
import io.github.airflux.parser.AirFluxJsonModule
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.Response
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.simple.RequestReader
import io.github.airflux.sample.dto.writer.ResponseWriter
import io.github.airflux.value.JsValue
import java.math.BigDecimal

fun main() {
    val mapper = ObjectMapper().apply {
        registerModule(AirFluxJsonModule)
    }

    val json = mapper.readValue(jsonOfTender, JsValue::class.java)

    when (val result = read(from = json, using = RequestReader)) {
        is JsResult.Success -> println(result.value)
        is JsResult.Failure -> {
            val errors = result.errors
            println(errors)
        }
    }

    val value = Value(amount = BigDecimal("125.52"), currency = "USD")
    val response = Response(value = value)
    val output: JsValue = ResponseWriter.write(response)
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
