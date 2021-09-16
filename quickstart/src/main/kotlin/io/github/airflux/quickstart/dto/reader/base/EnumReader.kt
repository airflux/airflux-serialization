package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.quickstart.json.error.JsonErrors

inline fun <reified T : Enum<T>> JsReader<String>.asEnum(): JsReader<T> =
    JsReader { context, path, input ->
        read(context, path, input)
            .asEnum()
    }

inline fun <reified T : Enum<T>> JsResult<String>.asEnum(): JsResult<T> =
    this.asEnum(enumValues()) { text -> enumValueOf(text.toUpperCase()) }

fun <T : Enum<T>> JsResult<String>.asEnum(allowable: Array<T>, transform: (String) -> T): JsResult<T> =
    flatMap { text, path ->
        try {
            JsResult.Success(transform(text), path)
        } catch (ignored: Exception) {
            JsResult.Failure(
                path = path,
                error = JsonErrors.EnumCast(actual = text, expected = allowable.joinToString())
            )
        }
    }
