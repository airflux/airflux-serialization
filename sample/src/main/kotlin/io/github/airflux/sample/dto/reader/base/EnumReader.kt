package io.github.airflux.sample.dto.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.sample.json.validation.StringValidator

object EnumReader {
    inline fun <reified T : Enum<T>> readAsEnum(): JsReader<T> =
        JsReader { context, input ->
            PrimitiveReader.stringReader.read(context, input)
                .validation(context, StringValidator.isNotBlank)
                .flatMap { text ->
                    try {
                        JsResult.Success(enumValueOf(text.uppercase()))
                    } catch (ignored: Exception) {
                        JsResult.Failure(JsonErrors.EnumCast(actual = text, expected = enumValues<T>().joinToString()))
                    }
                }
        }
}
