package io.github.airflux.sample.dto.reader.base

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.readAsList
import io.github.airflux.reader.readNullable
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsValue

object ErrorBuilder {
    val PathMissing: () -> JsError = { JsonErrors.PathMissing }
    val InvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError = JsonErrors::InvalidType
}

object PrimitiveReader : BasePrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}

object PathReaders {

    fun <T : Any> readRequired(from: JsValue, byPath: JsPath, using: JsReader<T>): JsResult<T> =
        readRequired(from, byPath, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> readRequired(from: JsValue, byName: String, using: JsReader<T>): JsResult<T> =
        readRequired(from, byName, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> readNullable(from: JsValue, byPath: JsPath, using: JsReader<T>): JsResult<T?> =
        readNullable(from, byPath, using, ErrorBuilder.InvalidType)

    fun <T : Any> readNullable(from: JsValue, byName: String, using: JsReader<T>): JsResult<T?> =
        readNullable(from, byName, using, ErrorBuilder.InvalidType)
}

object CollectionReaders {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = JsReader { input ->
        readAsList(input, using, ErrorBuilder.InvalidType)
    }
}
