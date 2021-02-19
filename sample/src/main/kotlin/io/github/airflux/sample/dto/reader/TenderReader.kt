package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.base.applyIfNotNull
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.dto.reader.base.PathReaders.nullable
import io.github.airflux.sample.dto.reader.base.PathReaders.required
import io.github.airflux.sample.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val TenderReader: JsReader<Tender> = run {

    val titleIsNotEmpty = applyIfNotNull(isNotBlank)

    reader { input ->
        JsResult.Success(
            Tender(
                id = required(from = input, path = JsPath("id"), using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it },
                title = nullable(from = input, path = JsPath("title"), using = stringReader)
                    .validation(titleIsNotEmpty)
                    .onFailure { return@reader it },
                value = nullable(from = input, path = JsPath("value"), using = ValueReader)
                    .onFailure { return@reader it },
                lots = required(from = input, path = JsPath("lots"), using = LotsReader)
                    .onFailure { return@reader it }
            )
        )
    }
}
