package io.github.airflux.sample.dto.reader.simple

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.dto.model.LotStatus
import io.github.airflux.sample.dto.reader.simple.base.CollectionReader.list
import io.github.airflux.sample.dto.reader.simple.base.EnumReader
import io.github.airflux.sample.dto.reader.simple.base.PathReaders.readRequired
import io.github.airflux.sample.dto.reader.simple.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.sample.json.validation.ArrayValidator.isUnique
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val LotStatusReader = EnumReader.readAsEnum<LotStatus>()

val LotReader: JsReader<Lot, JsonErrors> = reader { input ->

    val id = readRequired(from = input, byName = "id", using = stringReader)
        .validation(isNotBlank)
        .onFailure { return@reader it }
    val status = readRequired(from = input, byPath = JsPath.empty / "status", using = LotStatusReader)
        .onFailure { return@reader it }
    val value = readRequired(from = input, byPath = JsPath.empty / "value", using = ValueReader)
        .onFailure { return@reader it }

    Lot(id = id, status = status, value = value).asSuccess()
}

val LotsReader = list(LotReader)
    .validation(isUnique { lot -> lot.id })
