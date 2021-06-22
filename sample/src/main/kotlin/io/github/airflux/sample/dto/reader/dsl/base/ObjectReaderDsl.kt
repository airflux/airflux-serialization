package io.github.airflux.sample.dto.reader.dsl.base

import io.github.airflux.dsl.reader.`object`.ObjectReader
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidators
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.sample.dto.reader.base.ErrorBuilder
import io.github.airflux.sample.json.validation.additionalProperties
import io.github.airflux.sample.json.validation.isNotEmptyObject
import io.github.airflux.sample.json.validation.maxProperties
import io.github.airflux.sample.json.validation.minProperties

private val DefaultObjectReaderConfig = ObjectReaderConfiguration.build {
    failFast = true
}

private val DefaultObjectValidators = JsObjectValidators.build {
    +additionalProperties
    -additionalProperties

    +minProperties(10)
    -minProperties

    +maxProperties(25)
    -maxProperties

    +isNotEmptyObject
    -isNotEmptyObject
}

val reader = ObjectReader(
    initialConfiguration = DefaultObjectReaderConfig,
    initialValidatorBuilders = DefaultObjectValidators,
    pathMissingErrorBuilder = ErrorBuilder.PathMissing,
    invalidTypeErrorBuilder = ErrorBuilder.InvalidType
)

inline fun <T> simpleBuilder(crossinline builder: (ObjectValuesMap) -> T): (ObjectValuesMap, JsResultPath) -> JsResult<T> =
    { v, p -> JsResult.Success(builder(v), p) }
