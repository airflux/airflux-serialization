package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.RequiredPathReader.Companion.required
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.json.validation.NumberValidator.min
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank
import java.math.BigDecimal

val ValueReader: JsReader<Value> = reader { input ->
    JsResult.Success(
        Value(
            amount = required(from = input, path = JsPath("amount"), using = BasePrimitiveReader.bigDecimal)
                .validation(min(BigDecimal("0.01")))
                .onFailure { return@reader it },
            currency = required(from = input, path = JsPath("currency"), using = BasePrimitiveReader.string)
                .validation(isNotBlank())
                .onFailure { return@reader it }
        )
    )
}

