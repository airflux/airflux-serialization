package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.read
import io.github.airflux.dsl.ReaderDsl.readRequired
import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.fx.fx
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val LotReader: JsReader<Lot> = run {
    val idAttributeReader = readRequired(byName = "id", using = BasePrimitiveReader.string)
        .validation(isNotBlank())
    val statusAttributeReader = readRequired(byName = "status", using = BasePrimitiveReader.string)
        .validation(isNotBlank())
    val valueAttributeReader = readRequired(byName = "value", using = ValueReader)

    reader { input ->
        JsResult.fx {
            val (id) = read(from = input, using = idAttributeReader)
            val (status) = read(from = input, using = statusAttributeReader)
            val (value) = read(from = input, using = valueAttributeReader)

            Lot(id = id, status = status, value = value)
        }
    }
}
