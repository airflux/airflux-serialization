package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
class IsNotEmptyObjectValidator(private val errorBuilder: ErrorBuilder) : JsObjectValidator.After {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        context: JsReaderContext,
        properties: List<JsReaderProperty>,
        objectValuesMap: ObjectValuesMap,
        input: JsObject
    ): JsErrors? = if (objectValuesMap.isEmpty) JsErrors.of(errorBuilder.build()) else null

    fun interface ErrorBuilder {
        fun build(): JsError
    }
}
