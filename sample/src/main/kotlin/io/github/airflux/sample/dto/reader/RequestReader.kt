package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.Request
import io.github.airflux.sample.dto.reader.base.PathReaders.readRequired

val RequestReader: JsReader<Request> = reader { input ->
    JsResult.Success(
        Request(
            tender = readRequired(from = input, byPath = JsPath("tender"), using = TenderReader)
                .onFailure { return@reader it })
    )
}
