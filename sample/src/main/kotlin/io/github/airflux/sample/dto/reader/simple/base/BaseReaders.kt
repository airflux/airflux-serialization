package io.github.airflux.sample.dto.reader.simple.base

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.readAsList
import io.github.airflux.reader.readOptional
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.reader.simple.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.sample.json.validation.StringValidator
import io.github.airflux.value.JsValue

object ErrorBuilder {
    val PathMissing: () -> JsonErrors = { JsonErrors.PathMissing }
    val InvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsonErrors = JsonErrors::InvalidType
}

object PrimitiveReader : BasePrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}

object PathReaders {

    fun <T : Any> readRequired(from: JsValue, byPath: JsPath, using: JsReader<T, JsonErrors>): JsResult<T, JsonErrors> =
        readRequired(from, byPath, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> readRequired(from: JsValue, byName: String, using: JsReader<T, JsonErrors>): JsResult<T, JsonErrors> =
        readRequired(from, byName, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> readOptional(
        from: JsValue,
        byPath: JsPath,
        using: JsReader<T, JsonErrors>
    ): JsResult<T?, JsonErrors> =
        readOptional(from, byPath, using, ErrorBuilder.InvalidType)

    fun <T : Any> readOptional(
        from: JsValue,
        byName: String,
        using: JsReader<T, JsonErrors>
    ): JsResult<T?, JsonErrors> =
        readOptional(from, byName, using, ErrorBuilder.InvalidType)
}

object CollectionReader {
    fun <T : Any> list(using: JsReader<T, JsonErrors>): JsReader<List<T>, JsonErrors> = JsReader { input ->
        readAsList(input, using, ErrorBuilder.InvalidType)
    }
}

object EnumReader {
    inline fun <reified T : Enum<T>> readAsEnum(): JsReader<T, JsonErrors> =
        JsReader { input ->
            stringReader.read(input)
                .validation(StringValidator.isNotBlank)
                .flatMap { text ->
                    try {
                        JsResult.Success(enumValueOf<T>(text.toUpperCase()))
                    } catch (ignored: Exception) {
                        JsResult.Failure(JsonErrors.EnumCast(actual = text, expected = enumValues<T>().joinToString()))
                    }
                }
        }
}
