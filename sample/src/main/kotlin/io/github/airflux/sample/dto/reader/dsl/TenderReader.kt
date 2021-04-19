package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectReaderConfig
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectValidations
import io.github.airflux.sample.dto.reader.dsl.base.PrimitiveReader.stringReader
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.dto.reader.dsl.base.simpleBuilder
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val TenderReader: JsReader<Tender, JsonErrors> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val id = attribute(name = "id", reader = stringReader).required().validation(isNotBlank)
    val title = attribute(name = "title", reader = TitleReader).optional()
    val value = attribute(name = "value", reader = ValueReader).optional()
    val lots = attribute(name = "lots", reader = LotsReader).required()

    typeBuilder = simpleBuilder { values ->
        Tender(
            id = values[id],
            title = values[title],
            value = values[value],
            lots = values[lots],
        )
    }
}
