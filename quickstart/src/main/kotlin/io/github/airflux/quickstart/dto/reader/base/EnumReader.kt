package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.result.JsResult

inline fun <reified T : Enum<T>> Reader<String>.asEnum(): Reader<T> =
    Reader { context, location, input ->
        read(context, location, input)
            .asEnum()
    }

inline fun <reified T : Enum<T>> JsResult<String>.asEnum(): JsResult<T> =
    this.asEnum(enumValues()) { text -> enumValueOf(text.uppercase()) }

fun <T : Enum<T>> JsResult<String>.asEnum(allowable: Array<T>, transform: (String) -> T): JsResult<T> =
    flatMap { location, text ->
        try {
            JsResult.Success(location, transform(text))
        } catch (ignored: Exception) {
            JsResult.Failure(
                location = location,
                error = JsonErrors.EnumCast(actual = text, expected = allowable.joinToString())
            )
        }
    }
