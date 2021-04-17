package io.github.airflux.sample.dto.reader.simple

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.base.applyIfNotNull
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.dto.reader.simple.base.PathReaders.readOptional
import io.github.airflux.sample.dto.reader.simple.base.PathReaders.readRequired
import io.github.airflux.sample.dto.reader.simple.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val TenderReader: JsReader<Tender> = run {

    val titleIsNotEmpty = applyIfNotNull(isNotBlank)

    reader { input ->
        JsResult.Success(
            Tender(
                id = readRequired(from = input, byName = "id", using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it },
                title = readOptional(from = input, byName = "title", using = stringReader)
                    .validation(titleIsNotEmpty)
                    .onFailure { return@reader it },
                value = readOptional(from = input, byPath = JsPath.empty / "value", using = ValueReader)
                    .onFailure { return@reader it },
                lots = readRequired(from = input, byPath = JsPath.empty / "lots", using = LotsReader)
                    .onFailure { return@reader it }
            )
        )
    }
}
