package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.quickstart.json.error.JsonErrors
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.flatMapResult
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.success

inline fun <EB, CTX, reified T : Enum<T>> Reader<EB, CTX, String>.asEnum(): Reader<EB, CTX, T> =
    flatMapResult { _, location, value ->
        try {
            enumValueOf<T>(value.uppercase()).success()
        } catch (ignored: Exception) {
            val allowable = enumValues<T>()
            ReaderResult.Failure(
                location = location,
                error = JsonErrors.EnumCast(actual = value, expected = allowable.joinToString())
            )
        }
    }
