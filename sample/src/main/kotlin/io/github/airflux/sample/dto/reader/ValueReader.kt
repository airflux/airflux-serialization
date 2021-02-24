package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.base.PathReaders.readRequired
import io.github.airflux.sample.dto.reader.base.PrimitiveReader.bigDecimalReader
import io.github.airflux.sample.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.validation.NumberValidator.gt
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank
import java.math.BigDecimal

val ValueReader: JsReader<Value> = run {
    val amountMoreZero = gt(BigDecimal("0.01"))

    reader { input ->
        JsResult.Success(
            Value(
                amount = readRequired(from = input, byPath = JsPath("amount"), using = bigDecimalReader)
                    .validation(amountMoreZero)
                    .onFailure { return@reader it },

                currency = readRequired(from = input, byPath = JsPath("currency"), using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it }
            )
        )
    }
}
