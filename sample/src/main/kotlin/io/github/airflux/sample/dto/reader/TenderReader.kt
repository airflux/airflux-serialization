package io.github.airflux.sample.dto.reader

import io.github.airflux.dsl.ReaderDsl
import io.github.airflux.dsl.ReaderDsl.read
import io.github.airflux.dsl.ReaderDsl.readNullable
import io.github.airflux.dsl.ReaderDsl.readRequired
import io.github.airflux.dsl.ReaderDsl.readTraversable
import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.json.validation.ArrayValidator.isUnique
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val TenderReader = run {
    val idAttributeReader = readRequired(byName = "id", using = BasePrimitiveReader.string)
        .validation(isNotBlank())
    val titleAttributeReader = readRequired(byName = "title", using = BasePrimitiveReader.string)
        .validation(isNotBlank())
    val valueAttributeReader = readNullable(byName = "value", using = ValueReader)
    val lotsAttributeReader = readTraversable(byName = "lots", using = LotReader)
        .validation(isUnique { it.id })

    ReaderDsl.reader { input ->
        JsResult.Success(
            Tender(
                id = read(from = input, using = idAttributeReader).onFailure { return@reader it },
                title = read(from = input, using = titleAttributeReader).onFailure { return@reader it },
                value = read(from = input, using = valueAttributeReader).onFailure { return@reader it },
                lots = read(from = input, using = lotsAttributeReader).onFailure { return@reader it }
            )
        )
    }
}
