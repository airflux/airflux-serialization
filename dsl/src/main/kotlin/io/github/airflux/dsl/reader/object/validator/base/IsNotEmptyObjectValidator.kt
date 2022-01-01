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
class IsNotEmptyObjectValidator(private val errorBuilder: ErrorBuilder) :
    JsObjectValidator.Identifier,
    JsObjectValidator.After.Builder {

    override val id = IsNotEmptyObjectValidator

    override fun build(
        configuration: ObjectReaderConfiguration,
        properties: List<JsReaderProperty>
    ): JsObjectValidator.After = lazy { Validator(errorBuilder) }.value

    private class Validator(val errorBuilder: ErrorBuilder) : JsObjectValidator.After {

        override fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext
        ): JsErrors? =
            if (objectValuesMap.isEmpty) JsErrors.of(errorBuilder.build()) else null
    }

    fun interface ErrorBuilder {
        fun build(): JsError
    }

    companion object Id : JsObjectValidator.Id<Validator>
}
