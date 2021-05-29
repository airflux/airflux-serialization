package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidator
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

fun minPropertiesValidator(
    minPropertiesErrorBuilder: MinPropertiesValidator.ErrorBuilder
): (Int) -> ObjectValidator.After.Builder =
    { min: Int ->
        MinPropertiesValidator.Builder(min, minPropertiesErrorBuilder)
    }

class MinPropertiesValidator private constructor(
    private val value: Int,
    private val errorBuilder: ErrorBuilder
) : ObjectValidator.After {

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

    class Builder internal constructor(
        private val value: Int,
        private val errorBuilder: ErrorBuilder
    ) : ObjectValidator.After.Builder {

        override val key: String = NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            properties: List<JsReaderProperty<*>>
        ): ObjectValidator.After = MinPropertiesValidator(value, errorBuilder)
    }

    fun interface ErrorBuilder {
        fun build(expected: Int, actual: Int): JsError
    }

    companion object {
        const val NAME: String = "MinPropertiesValidator"
    }
}