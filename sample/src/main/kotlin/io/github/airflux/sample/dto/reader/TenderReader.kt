package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.base.applyIfNotNull
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.dto.reader.base.PathReaders
import io.github.airflux.sample.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val TenderReader: JsReader<Tender> = run {

    val titleIsNotEmpty = applyIfNotNull(isNotBlank)

    reader { input ->
        JsResult.Success(
            Tender(
                id = PathReaders.required(from = input, byName = "id", using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it },
                title = PathReaders.nullable(from = input, byName = "title", using = stringReader)
                    .validation(titleIsNotEmpty)
                    .onFailure { return@reader it },
                value = PathReaders.nullable(from = input, byPath = JsPath("value"), using = ValueReader)
                    .onFailure { return@reader it },
                lots = PathReaders.required(from = input, byPath = JsPath("lots"), using = LotsReader)
                    .onFailure { return@reader it }
            )
        )
    }
}
