package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.quickstart.json.error.JsonErrors

inline fun <reified T : Enum<T>> JsReader<String>.asEnum(): JsReader<T> =
    JsReader { context, location, input ->
        read(context, location, input)
            .asEnum()
    }

inline fun <reified T : Enum<T>> JsResult<String>.asEnum(): JsResult<T> =
    this.asEnum(enumValues()) { text -> enumValueOf(text.uppercase()) }

fun <T : Enum<T>> JsResult<String>.asEnum(allowable: Array<T>, transform: (String) -> T): JsResult<T> =
    flatMap { text, location ->
        try {
            JsResult.Success(transform(text), location)
        } catch (ignored: Exception) {
            JsResult.Failure(
                location = location,
                error = JsonErrors.EnumCast(actual = text, expected = allowable.joinToString())
            )
        }
    }
