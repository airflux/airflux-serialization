package io.github.airflux.sample.dto.reader.dsl.base

import io.github.airflux.dsl.reader.`object`.ObjectReader
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidations
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.base.BasePrimitiveReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.readAsList
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.reader.simple.base.PrimitiveReader
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.sample.json.validation.StringValidator
import io.github.airflux.sample.json.validation.`object`.isNotEmpty

object ErrorBuilder {
    val PathMissing = PathMissingErrorBuilder { JsonErrors.PathMissing }
    val InvalidType = InvalidTypeErrorBuilder { expected, actual ->
        JsonErrors.InvalidType(expected, actual)
    }
}

object PrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}

object CollectionReader {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = JsReader { input ->
        readAsList(input, using, ErrorBuilder.InvalidType)
    }
}

object EnumReader {
    inline fun <reified T : Enum<T>> readAsEnum(): JsReader<T> =
        JsReader { input ->
            PrimitiveReader.stringReader.read(input)
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

private val DefaultObjectReaderConfig = ObjectReaderConfiguration.Builder()
    .apply {
        failFast = true
    }
    .build()

private val DefaultObjectValidations = ObjectValidations.Builder()
    .apply {
        isNotEmpty = true
    }

val reader = ObjectReader(
    initialConfiguration = DefaultObjectReaderConfig,
    initialValidations = DefaultObjectValidations,
    pathMissingErrorBuilder = ErrorBuilder.PathMissing,
    invalidTypeErrorBuilder = ErrorBuilder.InvalidType
)

inline fun <T> simpleBuilder(crossinline builder: (values: ObjectValuesMap) -> T): (ObjectValuesMap) -> JsResult<T> =
    { JsResult.Success(builder(it)) }
