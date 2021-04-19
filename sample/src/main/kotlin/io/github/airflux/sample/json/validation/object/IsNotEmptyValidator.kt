package io.github.airflux.sample.json.validation.`object`

import io.github.airflux.dsl.reader.`object`.Attribute
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidations
import io.github.airflux.dsl.reader.`object`.ObjectValidator
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.reader.result.JsError
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
var ObjectValidations.Builder<JsonErrors>.isNotEmpty: Boolean
    get() = IsNotEmptyValidator.NAME in after
    set(value) {
        if (value)
            after.add(IsNotEmptyValidator.Builder())
        else
            after.remove(IsNotEmptyValidator.NAME)
    }

class IsNotEmptyValidator private constructor() : ObjectValidator.After<JsonErrors> {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        input: JsObject,
        attributes: List<Attribute<*, JsonErrors>>,
        objectValuesMap: ObjectValuesMap<JsonErrors>
    ): List<JsonErrors> =
        if (objectValuesMap.isEmpty)
            listOf(JsonErrors.Validation.Object.IsEmpty)
        else
            emptyList()

    class Builder : ObjectValidator.After.Builder<JsonErrors> {

        override val key: String
            get() = NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            attributes: List<Attribute<*, JsonErrors>>
        ): ObjectValidator.After<JsonErrors> = validator
    }

    companion object {
        const val NAME: String = "IsNotEmptyValidator"

        private val validator = IsNotEmptyValidator()
    }
}
