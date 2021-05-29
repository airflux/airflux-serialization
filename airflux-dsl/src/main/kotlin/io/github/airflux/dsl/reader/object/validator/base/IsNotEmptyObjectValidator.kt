package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidator
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsObject

fun isNotEmptyObjectValidator(
    isNotEmptyObjectErrorBuilder: IsNotEmptyObjectValidator.ErrorBuilder
): ObjectValidator.After.Builder =
    IsNotEmptyObjectValidator.Builder(isNotEmptyObjectErrorBuilder)

class IsNotEmptyObjectValidator private constructor(private val errorBuilder: ErrorBuilder) : ObjectValidator.After {

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

    class Builder internal constructor(private val errorBuilder: ErrorBuilder) : ObjectValidator.After.Builder {

        override val key: String
            get() = NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            properties: List<JsReaderProperty<*>>
        ): ObjectValidator.After = lazy { IsNotEmptyObjectValidator(errorBuilder) }.value
    }

    fun interface ErrorBuilder {
        fun build(): JsError
    }

    companion object {
        const val NAME: String = "IsNotEmptyValidator"
    }
}
