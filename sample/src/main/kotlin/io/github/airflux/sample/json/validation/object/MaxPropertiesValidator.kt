package io.github.airflux.sample.json.validation.`object`

import io.github.airflux.dsl.reader.`object`.Attribute
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValidations
import io.github.airflux.dsl.reader.`object`.ObjectValidator
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.sample.json.error.JsonErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
var ObjectValidations.Builder<JsonErrors>.maxProperties: Int
    get() = after[MaxPropertiesValidator.NAME]
        ?.let { (it as MaxPropertiesValidator.Builder).value }
        ?: -1
    set(value) {
        if (value > 0)
            after.add(MaxPropertiesValidator.Builder(value))
        else
            after.remove(MaxPropertiesValidator.NAME)
    }

class MaxPropertiesValidator private constructor(val value: Int) : ObjectValidator.After<JsonErrors> {

    override fun validation(
        configuration: ObjectReaderConfiguration,
        input: JsObject,
        attributes: List<Attribute<*, JsonErrors>>,
        objectValuesMap: ObjectValuesMap<JsonErrors>
    ): List<JsonErrors> =
        if (objectValuesMap.size > value)
            listOf(JsonErrors.Validation.Object.MaxProperties(expected = value, actual = objectValuesMap.size))
        else
            emptyList()

    class Builder(val value: Int) : ObjectValidator.After.Builder<JsonErrors> {

        override val key: String
            get() = NAME

        override fun build(
            configuration: ObjectReaderConfiguration,
            attributes: List<Attribute<*, JsonErrors>>
        ): ObjectValidator.After<JsonErrors> = MaxPropertiesValidator(value)
    }

    companion object {
        const val NAME = "MinPropertiesValidator"
    }
}
