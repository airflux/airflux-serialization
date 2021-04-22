package io.github.airflux.sample.dto.reader.simple

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.sample.dto.Request
import io.github.airflux.sample.dto.reader.simple.base.readRequired

val RequestReader: JsReader<Request> = reader { input ->
    Request(
        tender = readRequired(from = input, byPath = JsPath.empty / "tender", using = TenderReader)
            .onFailure { return@reader it }
    ).asSuccess()
}
