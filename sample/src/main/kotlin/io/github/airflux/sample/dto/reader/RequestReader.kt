package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl.read
import io.github.airflux.dsl.ReaderDsl.readRequired
import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.Request

val RequestReader = run {
    val tenderAttributeReader = readRequired(byName = "tender", using = TenderReader)

    reader { input ->
        JsResult.Success(
            Request(
                tender = read(from = input, using = tenderAttributeReader).onFailure { return@reader it }
            )
        )
    }
}
