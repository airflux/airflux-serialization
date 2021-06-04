package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidator
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

class IsNotEmptyObject(private val errorBuilder: ErrorBuilder) :
    ObjectValidator.Identifier,
    ObjectValidator.After.Builder {

    override val id = IsNotEmptyObject

    override fun build(
        configuration: ObjectReaderConfiguration,
        properties: List<JsReaderProperty<*>>
    ): ObjectValidator.After = lazy { Validator(errorBuilder) }.value

    private class Validator(val errorBuilder: ErrorBuilder) : ObjectValidator.After {

        override fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext?
        ): List<JsError> =
            if (objectValuesMap.isEmpty)
                listOf(errorBuilder.build())
            else
                emptyList()
    }

    fun interface ErrorBuilder {
        fun build(): JsError
    }

    companion object Id : ObjectValidator.Id<Validator>
}
