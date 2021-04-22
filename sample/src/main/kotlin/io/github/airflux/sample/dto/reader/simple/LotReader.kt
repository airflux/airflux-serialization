package io.github.airflux.sample.dto.reader.simple

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.fx.fx
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.dto.model.LotStatus
import io.github.airflux.sample.dto.reader.base.CollectionReader.list
import io.github.airflux.sample.dto.reader.base.EnumReader
import io.github.airflux.sample.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.sample.dto.reader.simple.base.readRequired
import io.github.airflux.sample.json.validation.ArrayValidator.isUnique
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val LotStatusReader = EnumReader.readAsEnum<LotStatus>()

val LotReader: JsReader<Lot> = reader { input ->
    JsResult.fx {
        val (id) = readRequired(from = input, byName = "id", using = stringReader)
            .validation(isNotBlank)
        val (status) = readRequired(from = input, byPath = JsPath.empty / "status", using = LotStatusReader)
        val (value) = readRequired(from = input, byPath = JsPath.empty / "value", using = ValueReader)

        Lot(id = id, status = status, value = value)
    }
}

val LotsReader = list(LotReader)
    .validation(isUnique { lot -> lot.id })
