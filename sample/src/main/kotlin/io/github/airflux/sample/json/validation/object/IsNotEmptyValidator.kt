package io.github.airflux.sample.json.validation.`object`

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.ObjectValidators
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
var ObjectValidators.Builder.isNotEmpty: Boolean
    get() = IsNotEmptyValidator.NAME in after
    set(value) {
        if (value)
            after.add(IsNotEmptyValidator.Builder())
        else
            after.remove(IsNotEmptyValidator.NAME)
    }

class IsNotEmptyValidator private constructor() : ObjectValidator.After {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        input: JsObject,
        properties: List<JsReaderProperty<*>>,
        objectValuesMap: ObjectValuesMap,
        context: JsReaderContext?
    ): List<JsError> =
        if (objectValuesMap.isEmpty)
            listOf(JsonErrors.Validation.Object.IsEmpty)
        else
            emptyList()

    class Builder : ObjectValidator.After.Builder {

        override val key: String
            get() = NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            properties: List<JsReaderProperty<*>>
        ): ObjectValidator.After = validator
    }

    companion object {
        const val NAME: String = "IsNotEmptyValidator"

        private val validator = IsNotEmptyValidator()
    }
}
