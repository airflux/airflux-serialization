package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.RequiredPathReader.Companion.required
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val TenderReader: JsReader<Tender> = reader { input ->
    JsResult.Success(
        Tender(
            id = required(from = input, path = JsPath("id"), using = BasePrimitiveReader.string)
                .validation(isNotBlank())
                .onFailure { return@reader it },
            title = required(from = input, path = JsPath("title"), using = BasePrimitiveReader.string)
                .validation(isNotBlank())
                .onFailure { return@reader it },
            value = required(from = input, path = JsPath("value"), using = ValueReader)
                .onFailure { return@reader it },
            lots = required(from = input, path = JsPath("lots"), using = LotsReader)
                .onFailure { return@reader it }
        )
    )
}
