package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidator
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

class MinProperties(private val errorBuilder: ErrorBuilder) : ObjectValidator.Identifier {

    override val id = MinProperties

    operator fun invoke(min: Int): ObjectValidator.After.Builder = Builder(min)

    private inner class Builder(val min: Int) : ObjectValidator.After.Builder {

        override val id: ObjectValidator.Id<*> = MinProperties

        override fun build(
            configuration: ObjectReaderConfiguration,
            properties: List<JsReaderProperty<*>>
        ): ObjectValidator.After = Validator(min, errorBuilder)
    }

    private class Validator(val value: Int, val errorBuilder: ErrorBuilder) : ObjectValidator.After {

        override fun validation(
            configuration: ObjectReaderConfiguration,
            input: JsObject,
            properties: List<JsReaderProperty<*>>,
            objectValuesMap: ObjectValuesMap,
            context: JsReaderContext?
        ): List<JsError> =
            if (objectValuesMap.size < value)
                listOf(errorBuilder.build(expected = value, actual = objectValuesMap.size))
            else
                emptyList()
    }

    fun interface ErrorBuilder {
        fun build(expected: Int, actual: Int): JsError
    }

    companion object Id : ObjectValidator.Id<Validator>
}
