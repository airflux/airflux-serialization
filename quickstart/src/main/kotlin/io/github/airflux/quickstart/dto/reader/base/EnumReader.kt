package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.result.ReaderResult

inline fun <reified T : Enum<T>> Reader<String>.asEnum(): Reader<T> =
    Reader { context, location, input ->
        read(context, location, input)
            .asEnum()
    }

inline fun <reified T : Enum<T>> ReaderResult<String>.asEnum(): ReaderResult<T> =
    this.asEnum(enumValues()) { text -> enumValueOf(text.uppercase()) }

fun <T : Enum<T>> ReaderResult<String>.asEnum(allowable: Array<T>, transform: (String) -> T): ReaderResult<T> =
    flatMap { location, text ->
        try {
            ReaderResult.Success(location, transform(text))
        } catch (ignored: Exception) {
            ReaderResult.Failure(
                location = location,
                error = JsonErrors.EnumCast(actual = text, expected = allowable.joinToString())
            )
        }
    }
