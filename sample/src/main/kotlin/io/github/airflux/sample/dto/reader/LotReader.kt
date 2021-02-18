package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.RequiredPathReader.Companion.required
import io.github.airflux.reader.TraversableReader.Companion.list
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.fx.fx
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.json.validation.ArrayValidator
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val LotReader: JsReader<Lot> = reader { input ->
    JsResult.fx {
        val (id) = required(from = input, path = JsPath("id"), BasePrimitiveReader.string)
            .validation(isNotBlank())
        val (status) = required(from = input, path = JsPath("status"), using = BasePrimitiveReader.string)
            .validation(isNotBlank())
        val (value) = required(from = input, path = JsPath("value"), using = ValueReader)

        Lot(id = id, status = status, value = value)
    }
}

val LotsReader = list(LotReader)
    .validation(ArrayValidator.isUnique { lot -> lot.id })
