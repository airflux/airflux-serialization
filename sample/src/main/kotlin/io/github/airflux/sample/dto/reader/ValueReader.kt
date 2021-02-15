package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.read
import io.github.airflux.dsl.ReaderDsl.readRequired
import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.json.validation.NumberValidator.min
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank
import java.math.BigDecimal

val ValueReader: JsReader<Value> = run {
    val amountAttributeReader = readRequired(byName = "amount", using = BasePrimitiveReader.bigDecimal)
        .validation(min(BigDecimal("0.01")))
    val currencyAttributeReader = readRequired(byName = "currency", using = BasePrimitiveReader.string)
        .validation(isNotBlank())

    reader { input ->
        JsResult.Success(
            Value(
                amount = read(from = input, using = amountAttributeReader).onFailure { return@reader it },
                currency = read(from = input, using = currencyAttributeReader).onFailure { return@reader it }
            )
        )
    }
}
