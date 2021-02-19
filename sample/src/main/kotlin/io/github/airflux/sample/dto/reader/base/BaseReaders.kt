package io.github.airflux.sample.dto.reader.base

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.NullablePathReader
import io.github.airflux.reader.RequiredPathReader
import io.github.airflux.reader.TraversableReader
import io.github.airflux.reader.base.BasePrimitiveReader
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

    fun <T : Any> required(from: JsValue, path: JsPath, using: JsReader<T>): JsResult<T> =
        RequiredPathReader.required(from, path, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> nullable(from: JsValue, path: JsPath, using: JsReader<T>): JsResult<T?> =
        NullablePathReader.nullable(from, path, using, ErrorBuilder.InvalidType)
}

object TraversableReaders {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = TraversableReader.list(using, ErrorBuilder.InvalidType)
}
